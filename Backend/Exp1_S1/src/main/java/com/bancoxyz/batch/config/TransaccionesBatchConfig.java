package com.bancoxyz.batch.config;

import com.bancoxyz.batch.listener.ResumenJobListener;
import com.bancoxyz.batch.model.Transaccion;
import com.bancoxyz.batch.model.TransaccionInput;
import com.bancoxyz.batch.processor.TransaccionProcessor;
import com.bancoxyz.batch.repository.ResumenTransaccionesRepository;
import com.bancoxyz.batch.repository.TransaccionRepository;
import com.bancoxyz.batch.tasklet.ResumenTransaccionesTasklet;
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
 * Configuracion del Job 1: Reporte de Transacciones Diarias.
 *
 * El Job se compone de DOS Steps encadenados:
 * 1. transaccionesStep: FlatFileItemReader -> TransaccionProcessor ->
 * RepositoryItemWriter. Lee el CSV, valida/normaliza y persiste las
 * transacciones validas.
 * 2. resumenTransaccionesStep: un Tasklet que consolida el resumen
 * (totales, montos por tipo, anomalias) y lo escribe a la tabla
 * resumen_transacciones y a un archivo CSV.
 *
 * Politicas de tolerancia a fallos en el step 1:
 * - skip: omite hasta 1000 filas que lancen excepcion de parseo, para
 * que una linea corrupta no aborte el job.
 * - retry: reintenta hasta 3 veces ante fallos transitorios de acceso a
 * datos (DataAccessException), utiles cuando la BD esta momentaneamente
 * ocupada o hay un bloqueo temporal.
 */
@Configuration
public class TransaccionesBatchConfig {

    @Value("classpath:${app.data.path}/transacciones.csv")
    private Resource transaccionesCsv;

    @Bean
    public FlatFileItemReader<TransaccionInput> transaccionReader() {
        return new FlatFileItemReaderBuilder<TransaccionInput>()
                .name("transaccionReader")
                .resource(transaccionesCsv)
                .linesToSkip(1)
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

    @Bean
    public Step transaccionesStep(JobRepository jobRepository,
            PlatformTransactionManager txManager,
            FlatFileItemReader<TransaccionInput> transaccionReader,
            ItemProcessor<TransaccionInput, Transaccion> transaccionProcessor,
            ItemWriter<Transaccion> transaccionWriter) {
        return new StepBuilder("transaccionesStep", jobRepository)
                .<TransaccionInput, Transaccion>chunk(10, txManager)
                .reader(transaccionReader)
                .processor(transaccionProcessor)
                .writer(transaccionWriter)
                .faultTolerant()
                .skipLimit(1000)
                .skip(Exception.class)
                .retryLimit(3)
                .retry(DataAccessException.class)
                .build();
    }

    @Bean
    public Step resumenTransaccionesStep(JobRepository jobRepository,
            PlatformTransactionManager txManager,
            TransaccionRepository transaccionRepository,
            ResumenTransaccionesRepository resumenRepository,
            @Value("${app.output.path}") String outputDir) {
        long totalLineas = contarLineasCsv();
        return new StepBuilder("resumenTransaccionesStep", jobRepository)
                .tasklet(new ResumenTransaccionesTasklet(
                        transaccionRepository, resumenRepository, outputDir, totalLineas), txManager)
                .build();
    }

    private long contarLineasCsv() {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(transaccionesCsv.getInputStream()))) {
            long lineas = reader.lines().count();
            return Math.max(0, lineas - 1);
        } catch (Exception e) {
            return 0;
        }
    }

    @Bean
    public Job reporteTransaccionesJob(JobRepository jobRepository,
            Step transaccionesStep,
            Step resumenTransaccionesStep) {
        return new JobBuilder("reporteTransaccionesJob", jobRepository)
                .listener(new ResumenJobListener())
                .start(transaccionesStep)
                .next(resumenTransaccionesStep)
                .build();
    }
}
