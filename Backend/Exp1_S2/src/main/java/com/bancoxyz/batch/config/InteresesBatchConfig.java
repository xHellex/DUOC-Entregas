package com.bancoxyz.batch.config;

import com.bancoxyz.batch.advanced.CustomDecider;
import com.bancoxyz.batch.advanced.CustomSkipPolicy;
import com.bancoxyz.batch.advanced.JobCompletionListener;
import com.bancoxyz.batch.advanced.RegistroSkipListener;
import com.bancoxyz.batch.model.CuentaInteres;
import com.bancoxyz.batch.model.InteresInput;
import com.bancoxyz.batch.processor.InteresProcessor;
import com.bancoxyz.batch.repository.CuentaInteresRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuracion del Job 2: Calculo de Intereses Mensuales (version S2).
 * Incorpora procesamiento paralelo (3 hilos, chunk 5), SkipPolicy
 * personalizada, SkipListener con archivo de errores, y Decider para la
 * politica de finalizacion y re-ejecucion.
 */
@Configuration
public class InteresesBatchConfig {

    @Value("classpath:${app.data.path}/intereses.csv")
    private Resource interesesCsv;

    @Value("${app.output.path}")
    private String outputDir;

    @Bean
    public SynchronizedItemStreamReader<InteresInput> interesReader() {
        FlatFileItemReader<InteresInput> flatReader = new FlatFileItemReaderBuilder<InteresInput>()
                .name("interesReader")
                .resource(interesesCsv)
                .linesToSkip(1)
                .delimited()
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .targetType(InteresInput.class)
                .build();
        SynchronizedItemStreamReader<InteresInput> syncReader = new SynchronizedItemStreamReader<>();
        syncReader.setDelegate(flatReader);
        return syncReader;
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
    public Step interesesStep(JobRepository jobRepository,
                              PlatformTransactionManager txManager,
                              SynchronizedItemStreamReader<InteresInput> interesReader,
                              ItemProcessor<InteresInput, CuentaInteres> interesProcessor,
                              ItemWriter<CuentaInteres> interesWriter,
                              CustomSkipPolicy customSkipPolicy,
                              ThreadPoolTaskExecutor taskExecutor) {
        return new StepBuilder("interesesStep", jobRepository)
                .<InteresInput, CuentaInteres>chunk(5, txManager)
                .reader(interesReader)
                .processor(interesProcessor)
                .writer(interesWriter)
                .faultTolerant()
                .skipPolicy(customSkipPolicy)
                .listener(new RegistroSkipListener(outputDir, "intereses"))
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job calculoInteresesJob(JobRepository jobRepository,
                                   Step interesesStep,
                                   CustomDecider customDecider) {
        return new JobBuilder("calculoInteresesJob", jobRepository)
                .listener(new JobCompletionListener())
                .start(interesesStep)
                .next(customDecider)
                    .on("COMPLETED").end()
                .from(customDecider)
                    .on("RETRY").to(interesesStep)
                .end()
                .build();
    }
}
