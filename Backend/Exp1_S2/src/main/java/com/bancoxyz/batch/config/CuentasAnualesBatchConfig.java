package com.bancoxyz.batch.config;

import com.bancoxyz.batch.advanced.CustomDecider;
import com.bancoxyz.batch.advanced.CustomSkipPolicy;
import com.bancoxyz.batch.advanced.JobCompletionListener;
import com.bancoxyz.batch.advanced.RegistroSkipListener;
import com.bancoxyz.batch.model.CuentaAnual;
import com.bancoxyz.batch.model.CuentaAnualInput;
import com.bancoxyz.batch.processor.CuentaAnualProcessor;
import com.bancoxyz.batch.repository.CuentaAnualRepository;
import com.bancoxyz.batch.repository.EstadoCuentaAnualRepository;
import com.bancoxyz.batch.tasklet.InformeAnualTasklet;
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
 * Configuracion del Job 3: Generacion de Estados de Cuenta Anuales (version S2).
 * Incorpora procesamiento paralelo (3 hilos, chunk 5), SkipPolicy
 * personalizada, SkipListener con archivo de errores, y Decider para la
 * politica de finalizacion y re-ejecucion. Mantiene el segundo Step que
 * genera el informe anual para auditorias.
 */
@Configuration
public class CuentasAnualesBatchConfig {

    @Value("classpath:${app.data.path}/cuentas_anuales.csv")
    private Resource cuentasAnualesCsv;

    @Value("${app.output.path}")
    private String outputDir;

    @Bean
    public SynchronizedItemStreamReader<CuentaAnualInput> cuentaAnualReader() {
        FlatFileItemReader<CuentaAnualInput> flatReader = new FlatFileItemReaderBuilder<CuentaAnualInput>()
                .name("cuentaAnualReader")
                .resource(cuentasAnualesCsv)
                .linesToSkip(1)
                .delimited()
                .names("cuentaId", "fecha", "transaccion", "monto", "descripcion")
                .targetType(CuentaAnualInput.class)
                .build();
        SynchronizedItemStreamReader<CuentaAnualInput> syncReader = new SynchronizedItemStreamReader<>();
        syncReader.setDelegate(flatReader);
        return syncReader;
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
                                   SynchronizedItemStreamReader<CuentaAnualInput> cuentaAnualReader,
                                   ItemProcessor<CuentaAnualInput, CuentaAnual> cuentaAnualProcessor,
                                   ItemWriter<CuentaAnual> cuentaAnualWriter,
                                   CustomSkipPolicy customSkipPolicy,
                                   ThreadPoolTaskExecutor taskExecutor) {
        return new StepBuilder("cuentasAnualesStep", jobRepository)
                .<CuentaAnualInput, CuentaAnual>chunk(5, txManager)
                .reader(cuentaAnualReader)
                .processor(cuentaAnualProcessor)
                .writer(cuentaAnualWriter)
                .faultTolerant()
                .skipPolicy(customSkipPolicy)
                .listener(new RegistroSkipListener(outputDir, "cuentas_anuales"))
                .taskExecutor(taskExecutor)
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
                                       Step cuentasAnualesStep,
                                       Step informeAnualStep,
                                       CustomDecider customDecider) {
        return new JobBuilder("estadosCuentaAnualesJob", jobRepository)
                .listener(new JobCompletionListener())
                .start(cuentasAnualesStep)
                .next(customDecider)
                    .on("COMPLETED").to(informeAnualStep)
                .from(customDecider)
                    .on("RETRY").to(cuentasAnualesStep)
                .end()
                .build();
    }
}
