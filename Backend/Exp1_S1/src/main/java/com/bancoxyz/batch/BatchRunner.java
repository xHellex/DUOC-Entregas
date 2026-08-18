package com.bancoxyz.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Lanza los tres Jobs de migracion en orden al iniciar la aplicacion.
 *
 * Se usa un runner explicito con JobLauncher (en lugar del arranque
 * automatico de Spring Boot) por dos razones:
 *   1. Garantiza el ORDEN de ejecucion: transacciones, luego intereses,
 *      luego estados anuales.
 *   2. Agrega un parametro unico (timestamp) a cada corrida, de modo que
 *      el job pueda re-ejecutarse sin chocar con una instancia previa
 *      ya registrada como COMPLETED en el JobRepository.
 */
@Component
public class BatchRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BatchRunner.class);

    private final JobLauncher jobLauncher;
    private final Job reporteTransaccionesJob;
    private final Job calculoInteresesJob;
    private final Job estadosCuentaAnualesJob;

    public BatchRunner(JobLauncher jobLauncher,
                       Job reporteTransaccionesJob,
                       Job calculoInteresesJob,
                       Job estadosCuentaAnualesJob) {
        this.jobLauncher = jobLauncher;
        this.reporteTransaccionesJob = reporteTransaccionesJob;
        this.calculoInteresesJob = calculoInteresesJob;
        this.estadosCuentaAnualesJob = estadosCuentaAnualesJob;
    }

    @Override
    public void run(String... args) throws Exception {
        lanzar("Job 1 - Reporte de Transacciones Diarias", reporteTransaccionesJob);
        lanzar("Job 2 - Calculo de Intereses Mensuales", calculoInteresesJob);
        lanzar("Job 3 - Estados de Cuenta Anuales", estadosCuentaAnualesJob);
    }

    private void lanzar(String titulo, Job job) throws Exception {
        log.info("");
        log.info("################################################################");
        log.info(" LANZANDO {}", titulo);
        log.info("################################################################");
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(job, params);
    }
}
