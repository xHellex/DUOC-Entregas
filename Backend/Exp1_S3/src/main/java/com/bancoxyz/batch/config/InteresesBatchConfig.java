package com.bancoxyz.batch.config;

import com.bancoxyz.batch.advanced.CustomSkipPolicy;
import com.bancoxyz.batch.advanced.JobCompletionListener;
import com.bancoxyz.batch.advanced.RangoPartitioner;
import com.bancoxyz.batch.advanced.RegistroSkipListener;
import com.bancoxyz.batch.model.CuentaInteres;
import com.bancoxyz.batch.model.InteresInput;
import com.bancoxyz.batch.processor.InteresProcessor;
import com.bancoxyz.batch.repository.CuentaInteresRepository;
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
 * Configuracion del Job 2: Calculo de Intereses Mensuales (version S3).
 * Escalado por particionamiento: RangoPartitioner + minionStep @StepScope +
 * TaskExecutorPartitionHandler. Conserva tolerancia a fallos y SkipListener.
 */
@Configuration
public class InteresesBatchConfig {

    @Value("classpath:${app.data.path}/intereses.csv")
    private Resource interesesCsv;

    @Value("${app.output.path}")
    private String outputDir;

    @Value("${app.grid.size}")
    private int gridSize;

    @Bean
    @StepScope
    public FlatFileItemReader<InteresInput> interesReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {
        int desde = start == null ? 0 : start;
        int hasta = end == null ? Integer.MAX_VALUE : end;
        int maxItems = hasta - desde + 1;
        return new FlatFileItemReaderBuilder<InteresInput>()
                .name("interesReader")
                .resource(interesesCsv)
                .linesToSkip(1 + desde)
                .maxItemCount(maxItems)
                .delimited()
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .targetType(InteresInput.class)
                .build();
    }

    @Bean
    public ItemProcessor<InteresInput, CuentaInteres> interesProcessor() {
        return new InteresProcessor();
    }

    @Bean
    public RepositoryItemWriter<CuentaInteres> interesWriter(CuentaInteresRepository repo) {
        return new RepositoryItemWriterBuilder<CuentaInteres>()
                .repository(repo)
                .methodName("save")
                .build();
    }

    @Bean
    public Partitioner interesPartitioner() {
        return new RangoPartitioner(ContadorRegistros.contar(interesesCsv));
    }

    @Bean
    public Step interesMinionStep(JobRepository jobRepository,
                                  PlatformTransactionManager txManager,
                                  FlatFileItemReader<InteresInput> interesReader,
                                  ItemProcessor<InteresInput, CuentaInteres> interesProcessor,
                                  ItemWriter<CuentaInteres> interesWriter,
                                  CustomSkipPolicy customSkipPolicy) {
        return new StepBuilder("interesMinionStep", jobRepository)
                .<InteresInput, CuentaInteres>chunk(5, txManager)
                .reader(interesReader)
                .processor(interesProcessor)
                .writer(interesWriter)
                .faultTolerant()
                .skipPolicy(customSkipPolicy)
                .retryLimit(3)
                .retry(DataAccessException.class)
                .listener(new RegistroSkipListener(outputDir, "intereses"))
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler interesPartitionHandler(
            Step interesMinionStep, TaskExecutor taskExecutor) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(interesMinionStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(gridSize);
        return handler;
    }

    @Bean
    public Step interesesStep(JobRepository jobRepository,
                              Partitioner interesPartitioner,
                              TaskExecutorPartitionHandler interesPartitionHandler) {
        return new StepBuilder("interesesStep", jobRepository)
                .partitioner("interesMinionStep", interesPartitioner)
                .partitionHandler(interesPartitionHandler)
                .build();
    }

    // ---- Step de limpieza: vacia la tabla destino para que el Job sea idempotente ----
    @Bean
    public Step limpiezaInteresesStep(JobRepository jobRepository,
                                      PlatformTransactionManager txManager,
                                      CuentaInteresRepository cuentaInteresRepository) {
        return new StepBuilder("limpiezaInteresesStep", jobRepository)
                .tasklet(new LimpiezaTablasTasklet(cuentaInteresRepository), txManager)
                .build();
    }

    @Bean
    public Job calculoInteresesJob(JobRepository jobRepository,
                                   Step limpiezaInteresesStep,
                                   Step interesesStep) {
        return new JobBuilder("calculoInteresesJob", jobRepository)
                .listener(new JobCompletionListener())
                .start(limpiezaInteresesStep)
                .next(interesesStep)
                .build();
    }
}
