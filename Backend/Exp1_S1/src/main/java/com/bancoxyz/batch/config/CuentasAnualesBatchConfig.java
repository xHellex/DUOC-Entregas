package com.bancoxyz.batch.config;

import com.bancoxyz.batch.listener.ResumenJobListener;
import com.bancoxyz.batch.model.CuentaAnual;
import com.bancoxyz.batch.model.CuentaAnualInput;
import com.bancoxyz.batch.processor.CuentaAnualProcessor;
import com.bancoxyz.batch.repository.CuentaAnualRepository;
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
import com.bancoxyz.batch.repository.EstadoCuentaAnualRepository;
import com.bancoxyz.batch.tasklet.InformeAnualTasklet;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuracion del Job 3: Generacion de Estados de Cuenta Anuales.
 * Cadena: FlatFileItemReader -> CuentaAnualProcessor -> RepositoryItemWriter.
 * Compila las operaciones anuales normalizadas que alimentan los informes
 * de auditoria.
 */
@Configuration
public class CuentasAnualesBatchConfig {

    @Value("classpath:${app.data.path}/cuentas_anuales.csv")
    private Resource cuentasAnualesCsv;

    @Bean
    public FlatFileItemReader<CuentaAnualInput> cuentaAnualReader() {
        return new FlatFileItemReaderBuilder<CuentaAnualInput>()
                .name("cuentaAnualReader")
                .resource(cuentasAnualesCsv)
                .linesToSkip(1)
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
    public Step cuentasAnualesStep(JobRepository jobRepository,
                                   PlatformTransactionManager txManager,
                                   FlatFileItemReader<CuentaAnualInput> cuentaAnualReader,
                                   ItemProcessor<CuentaAnualInput, CuentaAnual> cuentaAnualProcessor,
                                   ItemWriter<CuentaAnual> cuentaAnualWriter) {
        return new StepBuilder("cuentasAnualesStep", jobRepository)
                .<CuentaAnualInput, CuentaAnual>chunk(10, txManager)
                .reader(cuentaAnualReader)
                .processor(cuentaAnualProcessor)
                .writer(cuentaAnualWriter)
                .faultTolerant()
                .skipLimit(1000)
                .skip(Exception.class)
                .retryLimit(3)
                .retry(DataAccessException.class)
                .build();
    }

    @Bean
    public Step informeAnualStep(JobRepository jobRepository,
                                 PlatformTransactionManager txManager,
                                 CuentaAnualRepository cuentaAnualRepository,
                                 EstadoCuentaAnualRepository estadoRepository,
                                 @Value("${app.output.path}") String outputDir) {
        return new StepBuilder("informeAnualStep", jobRepository)
                .tasklet(new InformeAnualTasklet(
                        cuentaAnualRepository, estadoRepository, outputDir), txManager)
                .build();
    }

    @Bean
    public Job estadosCuentaAnualesJob(JobRepository jobRepository,
                                       Step cuentasAnualesStep,
                                       Step informeAnualStep) {
        return new JobBuilder("estadosCuentaAnualesJob", jobRepository)
                .listener(new ResumenJobListener())
                .start(cuentasAnualesStep)
                .next(informeAnualStep)
                .build();
    }
}
