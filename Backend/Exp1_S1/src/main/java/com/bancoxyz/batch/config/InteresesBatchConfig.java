package com.bancoxyz.batch.config;

import com.bancoxyz.batch.listener.ResumenJobListener;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuracion del Job 2: Calculo de Intereses Mensuales.
 * Cadena: FlatFileItemReader -> InteresProcessor -> RepositoryItemWriter.
 * El writer usa save; como CuentaInteres tiene @Id asignado (cuenta_id),
 * los duplicados del legacy (John Doe 101 y 106) sobrescriben en lugar de
 * duplicar filas, garantizando idempotencia por cuenta.
 */
@Configuration
public class InteresesBatchConfig {

    @Value("classpath:${app.data.path}/intereses.csv")
    private Resource interesesCsv;

    @Bean
    public FlatFileItemReader<InteresInput> interesReader() {
        return new FlatFileItemReaderBuilder<InteresInput>()
                .name("interesReader")
                .resource(interesesCsv)
                .linesToSkip(1)
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
    public Step interesesStep(JobRepository jobRepository,
                              PlatformTransactionManager txManager,
                              FlatFileItemReader<InteresInput> interesReader,
                              ItemProcessor<InteresInput, CuentaInteres> interesProcessor,
                              ItemWriter<CuentaInteres> interesWriter) {
        return new StepBuilder("interesesStep", jobRepository)
                .<InteresInput, CuentaInteres>chunk(10, txManager)
                .reader(interesReader)
                .processor(interesProcessor)
                .writer(interesWriter)
                .faultTolerant()
                .skipLimit(1000)
                .skip(Exception.class)
                .retryLimit(3)
                .retry(DataAccessException.class)
                .build();
    }

    @Bean
    public Job calculoInteresesJob(JobRepository jobRepository, Step interesesStep) {
        return new JobBuilder("calculoInteresesJob", jobRepository)
                .listener(new ResumenJobListener())
                .start(interesesStep)
                .build();
    }
}
