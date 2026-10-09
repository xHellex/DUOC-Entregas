package com.bancoxyz.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;

/**
 * Imprime un resumen legible al terminar cada Job: estado final y, por cada
 * Step, cuantos registros se leyeron, escribieron, filtraron y omitieron.
 * Esto genera la evidencia de ejecucion que pide la actividad.
 */
public class ResumenJobListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(ResumenJobListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        String nombre = jobExecution.getJobInstance().getJobName();
        log.info("==================================================================");
        log.info(" RESUMEN DEL JOB: {}", nombre);
        log.info(" Estado final: {}", jobExecution.getStatus());
        for (StepExecution step : jobExecution.getStepExecutions()) {
            log.info(" ----------------------------------------------------------------");
            log.info(" Step: {}", step.getStepName());
            boolean esChunk = step.getReadCount() > 0 || step.getWriteCount() > 0
                    || step.getFilterCount() > 0 || step.getSkipCount() > 0;
            if (esChunk) {
                log.info("   Tipo                 : procesamiento (chunk)");
                log.info("   Leidos      (read)   : {}", step.getReadCount());
                log.info("   Escritos    (write)  : {}", step.getWriteCount());
                log.info("   Filtrados   (filter) : {}", step.getFilterCount());
                log.info("   Omitidos    (skip)   : {}", step.getSkipCount());
            } else {
                log.info("   Tipo                 : consolidacion (tasklet)");
                log.info("   Estado               : {}", step.getStatus());
                log.info("   (genera resumen/informe: ver logs [RESUMEN]/[INFORME] y carpeta salida/)");
            }
        }
        log.info("==================================================================");
    }
}
