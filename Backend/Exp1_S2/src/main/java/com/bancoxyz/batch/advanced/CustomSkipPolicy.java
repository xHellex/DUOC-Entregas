package com.bancoxyz.batch.advanced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

/**
 * Politica de omision (SkipPolicy) personalizada para el manejo de errores
 * en los tres Jobs del Banco XYZ.
 *
 * Decide, para cada excepcion, si la fila debe omitirse o si el error debe
 * detener el Job. Se omiten hasta un limite de LIMITE_OMISIONES:
 *   - FlatFileParseException: la linea del CSV no pudo parsearse (columnas
 *     corruptas, tipos no convertibles).
 *   - InvalidDataException: la fila viola una regla de negocio detectada en
 *     el ItemProcessor.
 * Cualquier otra excepcion, o superar el limite, detiene el proceso para no
 * enmascarar fallos graves.
 */
@Component
public class CustomSkipPolicy implements SkipPolicy {

    private static final Logger log = LoggerFactory.getLogger(CustomSkipPolicy.class);
    private static final int LIMITE_OMISIONES = 1000;

    @Override
    public boolean shouldSkip(Throwable t, long skipCount) {
        if ((t instanceof FlatFileParseException || t instanceof InvalidDataException)
                && skipCount < LIMITE_OMISIONES) {
            log.warn("[SKIP] Excepcion omitida ({} de {}): {}",
                    skipCount + 1, LIMITE_OMISIONES, t.getMessage());
            return true;
        }
        log.error("[SKIP] Excepcion NO omitida, se detiene el proceso: {}",
                t.getClass().getSimpleName());
        return false;
    }
}
