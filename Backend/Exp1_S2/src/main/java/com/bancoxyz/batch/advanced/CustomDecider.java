package com.bancoxyz.batch.advanced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

/**
 * Decider que implementa la politica de finalizacion y re-ejecucion.
 *
 * Tras ejecutar el step de procesamiento, evalua su resultado:
 *   - Si el step no registro excepciones de fallo, devuelve COMPLETED y el
 *     Job finaliza normalmente.
 *   - Si el step registro fallos, devuelve el estado personalizado RETRY,
 *     que el flujo del Job usa para volver a ejecutar el step.
 * Esto aporta resiliencia: un fallo recuperable no deja el proceso a medias.
 */
@Component
public class CustomDecider implements JobExecutionDecider {

    private static final Logger log = LoggerFactory.getLogger(CustomDecider.class);

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        if (stepExecution != null && stepExecution.getFailureExceptions().isEmpty()) {
            log.info("[DECIDER] Step sin fallos: se finaliza con COMPLETED");
            return FlowExecutionStatus.COMPLETED;
        }
        log.warn("[DECIDER] Step con fallos: se solicita RETRY");
        return new FlowExecutionStatus("RETRY");
    }
}
