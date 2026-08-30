package com.bancoxyz.batch.config;

import com.bancoxyz.batch.advanced.CustomSkipPolicy;
import com.bancoxyz.batch.advanced.JobCompletionListener;
import com.bancoxyz.batch.advanced.RangoPartitioner;
import com.bancoxyz.batch.advanced.RegistroSkipListener;
import com.bancoxyz.batch.model.CuentaAnual;
import com.bancoxyz.batch.model.CuentaAnualInput;
import com.bancoxyz.batch.processor.CuentaAnualProcessor;
import com.bancoxyz.batch.repository.CuentaAnualRepository;
import com.bancoxyz.batch.repository.EstadoCuentaAnualRepository;
import com.bancoxyz.batch.tasklet.InformeAnualTasklet;
import com.bancoxyz.batch.tasklet.LimpiezaTablasTasklet;
import com.bancoxyz.batch.util.ContadorRegistros;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuracion del Job 3: Estados de Cuenta Anuales (version S3).
 * Escalado por particionamiento en el step de procesamiento; conserva el
 * segundo step (Tasklet) que compila el informe anual para auditorias.
 */
@Configuration
public class CuentasAnualesBatchConfig {

    @Value("classpath:${app.data.path}/cuentas_anuales.csv")
    private Resource cuentasAnualesCsv;

    @Value("${app.output.path}")
    private String outputDir;

    @Value("${app.grid.size}")
    private int gridSize;

    @Bean
    @StepScope
    public FlatFileItemReader<CuentaAnualInput> cuentaAnualReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {
        int desde = start == null ? 0 : start;
        int hasta = end == null ? Integer.MAX_VALUE : end;
        int maxItems = hasta - desde + 1;
        return new FlatFileItemReaderBuilder<CuentaAnualInput>()
                .name("cuentaAnualReader")
                .resource(cuentasAnualesCsv)
                .linesToSkip(1 + desde)
                .maxItemCount(maxItems)
                .delimited()
                .names("cuentaId", "fecha", "transaccion", "monto", "descripcion")
                .targetType(CuentaAnualInput.class)
                .build();
    }

    @Bean
    public ItemProcessor<CuentaAnualInput, CuentaAnual> cuentaAnualProcessor() {
        return new CuentaAnualProcessor();
    }

    @Bean
    public RepositoryItemWriter<CuentaAnual> cuentaAnualWriter(CuentaAnualRepository repo) {
        return new RepositoryItemWriterBuilder<CuentaAnual>()
                .repository(repo)
                .methodName("save")
                .build();
    }

    @Bean
    public Partitioner cuentaAnualPartitioner() {
        return new RangoPartitioner(ContadorRegistros.contar(cuentasAnualesCsv));
    }

    @Bean
    public Step cuentaAnualMinionStep(JobRepository jobRepository,
                                      PlatformTransactionManager txManager,
                                      FlatFileItemReader<CuentaAnualInput> cuentaAnualReader,
                                      ItemProcessor<CuentaAnualInput, CuentaAnual> cuentaAnualProcessor,
                                      ItemWriter<CuentaAnual> cuentaAnualWriter,
                                      CustomSkipPolicy customSkipPolicy) {
        return new StepBuilder("cuentaAnualMinionStep", jobRepository)
                .<CuentaAnualInput, CuentaAnual>chunk(5, txManager)
                .reader(cuentaAnualReader)
                .processor(cuentaAnualProcessor)
                .writer(cuentaAnualWriter)
                .faultTolerant()
                .skipPolicy(customSkipPolicy)
                .retryLimit(3)
                .retry(DataAccessException.class)
                .listener(new RegistroSkipListener(outputDir, "cuentas_anuales"))
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler cuentaAnualPartitionHandler(
            Step cuentaAnualMinionStep, TaskExecutor taskExecutor) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(cuentaAnualMinionStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(gridSize);
        return handler;
    }

    @Bean
    public Step cuentasAnualesStep(JobRepository jobRepository,
                                   Partitioner cuentaAnualPartitioner,
                                   TaskExecutorPartitionHandler cuentaAnualPartitionHandler) {
        return new StepBuilder("cuentasAnualesStep", jobRepository)
                .partitioner("cuentaAnualMinionStep", cuentaAnualPartitioner)
                .partitionHandler(cuentaAnualPartitionHandler)
                .build();
    }

    // ---- Step de limpieza: vacia las tablas destino para que el Job sea idempotente ----
    @Bean
    public Step limpiezaCuentasAnualesStep(JobRepository jobRepository,
                                           PlatformTransactionManager txManager,
                                           CuentaAnualRepository cuentaAnualRepository,
                                           EstadoCuentaAnualRepository estadoRepository) {
        return new StepBuilder("limpiezaCuentasAnualesStep", jobRepository)
                .tasklet(new LimpiezaTablasTasklet(cuentaAnualRepository, estadoRepository), txManager)
                .build();
    }

    @Bean
    public Step informeAnualStep(JobRepository jobRepository,
                                 PlatformTransactionManager txManager,
                                 CuentaAnualRepository cuentaAnualRepository,
                                 EstadoCuentaAnualRepository estadoRepository) {
        return new StepBuilder("informeAnualStep", jobRepository)
                .tasklet(new InformeAnualTasklet(
                        cuentaAnualRepository, estadoRepository, outputDir), txManager)
                .build();
    }

    @Bean
    public Job estadosCuentaAnualesJob(JobRepository jobRepository,
                                       Step limpiezaCuentasAnualesStep,
                                       Step cuentasAnualesStep,
                                       Step informeAnualStep) {
        return new JobBuilder("estadosCuentaAnualesJob", jobRepository)
                .listener(new JobCompletionListener())
                .start(limpiezaCuentasAnualesStep)
                .next(cuentasAnualesStep)
                .next(informeAnualStep)
                .build();
    }
}
