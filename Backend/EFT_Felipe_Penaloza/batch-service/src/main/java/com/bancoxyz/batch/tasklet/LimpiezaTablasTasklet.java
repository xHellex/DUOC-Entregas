package com.bancoxyz.batch.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Primer Step de cada Job: vacia las tablas destino antes de procesar.
 *
 * Hace que la ejecucion sea IDEMPOTENTE: re-ejecutar un Job produce siempre
 * el mismo resultado, sin acumular filas de corridas anteriores y sin
 * distorsionar los conteos del resumen (total_validas vs total_leidas).
 * Es parte de la politica de reejecucion de jobs exigida por la actividad.
 */
public class LimpiezaTablasTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(LimpiezaTablasTasklet.class);

    private final JpaRepository<?, ?>[] repositorios;

    public LimpiezaTablasTasklet(JpaRepository<?, ?>... repositorios) {
        this.repositorios = repositorios;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        long total = 0;
        for (JpaRepository<?, ?> repo : repositorios) {
            total += repo.count();
            repo.deleteAllInBatch();
        }
        log.info("[LIMPIEZA] {} fila(s) eliminada(s) de {} tabla(s) destino antes de procesar",
                total, repositorios.length);
        return RepeatStatus.FINISHED;
    }
}
