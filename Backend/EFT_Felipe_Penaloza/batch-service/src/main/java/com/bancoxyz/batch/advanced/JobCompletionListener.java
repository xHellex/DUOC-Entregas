package com.bancoxyz.batch.advanced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.BatchStatus;

/**
 * Listener a nivel de Job que registra el inicio y el fin de la ejecucion,
 * junto con un resumen del estado final. Complementa las tecnicas de logs
 * exigidas por la pauta, permitiendo trazar cuando comienza y termina cada
 * Job y con que resultado.
 */
public class JobCompletionListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("[JOB] Inicio del Job '{}' con ID de ejecucion: {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String nombre = jobExecution.getJobInstance().getJobName();
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("[JOB] Job '{}' completado exitosamente. Estado: {}",
                    nombre, jobExecution.getStatus());
        } else {
            log.warn("[JOB] Job '{}' finalizo con estado: {}",
                    nombre, jobExecution.getStatus());
        }
        log.info("[JOB] Resumen -> inicio: {}, fin: {}, duracion aprox: {} ms",
                jobExecution.getStartTime(),
                jobExecution.getEndTime(),
                (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null)
                        ? java.time.Duration.between(jobExecution.getStartTime(),
                            jobExecution.getEndTime()).toMillis()
                        : 0);
    }
}
