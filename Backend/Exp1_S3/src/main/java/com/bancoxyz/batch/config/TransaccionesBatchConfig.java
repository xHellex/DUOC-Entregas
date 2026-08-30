package com.bancoxyz.batch.config;

import com.bancoxyz.batch.advanced.CustomSkipPolicy;
import com.bancoxyz.batch.advanced.JobCompletionListener;
import com.bancoxyz.batch.advanced.RangoPartitioner;
import com.bancoxyz.batch.advanced.RegistroSkipListener;
import com.bancoxyz.batch.model.Transaccion;
import com.bancoxyz.batch.model.TransaccionInput;
import com.bancoxyz.batch.processor.TransaccionProcessor;
import com.bancoxyz.batch.repository.ResumenTransaccionesRepository;
import com.bancoxyz.batch.repository.TransaccionRepository;
import com.bancoxyz.batch.tasklet.LimpiezaTablasTasklet;
import com.bancoxyz.batch.tasklet.ResumenTransaccionesTasklet;
import com.bancoxyz.batch.util.ContadorRegistros;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuracion del Job 1: Reporte de Transacciones Diarias (version S3).
 *
 * Implementa ESCALADO POR PARTICIONAMIENTO:
 *   - RangoPartitioner divide los registros en gridSize particiones (rangos
 *     start-end), definido por app.grid.size.
 *   - transaccionMinionStep es el paso "worker" que procesa una particion;
 *     su reader es @StepScope y lee solo su rango del CSV.
 *   - TaskExecutorPartitionHandler ejecuta las particiones en paralelo sobre
 *     el TaskExecutor.
 *   - transaccionesStep (PartitionStep) es el paso principal que orquesta.
 *
 * Conserva la tolerancia a fallos (CustomSkipPolicy, retry, SkipListener) y
 * el segundo step que genera el resumen.
 */
@Configuration
public class TransaccionesBatchConfig {

    @Value("classpath:${app.data.path}/transacciones.csv")
    private Resource transaccionesCsv;

    @Value("${app.output.path}")
    private String outputDir;

    @Value("${app.grid.size}")
    private int gridSize;

    // ---- Reader particionado: lee solo el rango [start, end] de su particion ----
    @Bean
    @StepScope
    public FlatFileItemReader<TransaccionInput> transaccionReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {
        int desde = start == null ? 0 : start;
        int hasta = end == null ? Integer.MAX_VALUE : end;
        int maxItems = hasta - desde + 1;
        return new FlatFileItemReaderBuilder<TransaccionInput>()
                .name("transaccionReader")
                .resource(transaccionesCsv)
                .linesToSkip(1 + desde)
                .maxItemCount(maxItems)
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .targetType(TransaccionInput.class)
                .build();
    }

    @Bean
    public ItemProcessor<TransaccionInput, Transaccion> transaccionProcessor() {
        return new TransaccionProcessor();
    }

    @Bean
    public RepositoryItemWriter<Transaccion> transaccionWriter(TransaccionRepository repo) {
        return new RepositoryItemWriterBuilder<Transaccion>()
                .repository(repo)
                .methodName("save")
                .build();
    }

    // ---- Particionador ----
    @Bean
    public Partitioner transaccionPartitioner() {
        int total = ContadorRegistros.contar(transaccionesCsv);
        return new RangoPartitioner(total);
    }

    // ---- Worker step (procesa una particion) ----
    @Bean
    public Step transaccionMinionStep(JobRepository jobRepository,
                                      PlatformTransactionManager txManager,
                                      FlatFileItemReader<TransaccionInput> transaccionReader,
                                      ItemProcessor<TransaccionInput, Transaccion> transaccionProcessor,
                                      ItemWriter<Transaccion> transaccionWriter,
                                      CustomSkipPolicy customSkipPolicy) {
        return new StepBuilder("transaccionMinionStep", jobRepository)
                .<TransaccionInput, Transaccion>chunk(5, txManager)
                .reader(transaccionReader)
                .processor(transaccionProcessor)
                .writer(transaccionWriter)
                .faultTolerant()
                .skipPolicy(customSkipPolicy)
                .retryLimit(3)
                .retry(DataAccessException.class)
                .listener(new RegistroSkipListener(outputDir, "transacciones"))
                .build();
    }

    // ---- PartitionHandler: ejecuta las particiones en paralelo ----
    @Bean
    public TaskExecutorPartitionHandler transaccionPartitionHandler(
            Step transaccionMinionStep, TaskExecutor taskExecutor) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(transaccionMinionStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(gridSize);
        return handler;
    }

    // ---- PartitionStep principal ----
    @Bean
    public Step transaccionesStep(JobRepository jobRepository,
                                  Partitioner transaccionPartitioner,
                                  TaskExecutorPartitionHandler transaccionPartitionHandler) {
        return new StepBuilder("transaccionesStep", jobRepository)
                .partitioner("transaccionMinionStep", transaccionPartitioner)
                .partitionHandler(transaccionPartitionHandler)
                .build();
    }

    // ---- Step de limpieza: vacia las tablas destino para que el Job sea idempotente ----
    @Bean
    public Step limpiezaTransaccionesStep(JobRepository jobRepository,
                                          PlatformTransactionManager txManager,
                                          TransaccionRepository transaccionRepository,
                                          ResumenTransaccionesRepository resumenRepository) {
        return new StepBuilder("limpiezaTransaccionesStep", jobRepository)
                .tasklet(new LimpiezaTablasTasklet(transaccionRepository, resumenRepository), txManager)
                .build();
    }

    // ---- Step de resumen ----
    @Bean
    public Step resumenTransaccionesStep(JobRepository jobRepository,
                                         PlatformTransactionManager txManager,
                                         TransaccionRepository transaccionRepository,
                                         ResumenTransaccionesRepository resumenRepository) {
        int total = ContadorRegistros.contar(transaccionesCsv);
        return new StepBuilder("resumenTransaccionesStep", jobRepository)
                .tasklet(new ResumenTransaccionesTasklet(
                        transaccionRepository, resumenRepository, outputDir, total), txManager)
                .build();
    }

    @Bean
    public Job reporteTransaccionesJob(JobRepository jobRepository,
                                       Step limpiezaTransaccionesStep,
                                       Step transaccionesStep,
                                       Step resumenTransaccionesStep) {
        return new JobBuilder("reporteTransaccionesJob", jobRepository)
                .listener(new JobCompletionListener())
                .start(limpiezaTransaccionesStep)
                .next(transaccionesStep)
                .next(resumenTransaccionesStep)
                .build();
    }
}
