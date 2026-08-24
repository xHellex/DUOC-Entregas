package com.bancoxyz.batch.processor;

import com.bancoxyz.batch.model.Transaccion;
import com.bancoxyz.batch.model.TransaccionInput;
import com.bancoxyz.batch.util.FechaLegacyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.util.Set;

/**
 * Procesa cada transaccion cruda del CSV y decide si es valida.
 *
 * Reglas de negocio para detectar anomalias (Job 1):
 *   - monto vacio, no numerico, negativo o cero  -> se omite
 *   - tipo distinto de "debito" o "credito"      -> se omite
 *     (el legacy trae "invalid" y "desconocido")
 *   - fecha nula o invalida, ej. 2024-13-01      -> se omite
 *
 * Devolver null hace que Spring Batch NO envie el item al writer:
 * es el mecanismo estandar de filtrado (los items filtrados se
 * contabilizan en filterCount, distinto de skipCount).
 */
public class TransaccionProcessor implements ItemProcessor<TransaccionInput, Transaccion> {

    private static final Logger log = LoggerFactory.getLogger(TransaccionProcessor.class);
    private static final Set<String> TIPOS_VALIDOS = Set.of("debito", "credito");

    @Override
    public Transaccion process(TransaccionInput input) {
        // Validacion de monto
        Double monto = aDouble(input.getMonto());
        if (monto == null || monto <= 0) {
            log.warn("[ANOMALIA] Transaccion id={} descartada: monto invalido '{}'",
                    input.getId(), input.getMonto());
            return null;
        }

        // Validacion de tipo
        String tipo = input.getTipo() == null ? "" : input.getTipo().trim().toLowerCase();
        if (!TIPOS_VALIDOS.contains(tipo)) {
            log.warn("[ANOMALIA] Transaccion id={} descartada: tipo invalido '{}'",
                    input.getId(), input.getTipo());
            return null;
        }

        // Validacion y normalizacion de fecha
        LocalDate fecha = FechaLegacyParser.parsear(input.getFecha());
        if (fecha == null) {
            log.warn("[ANOMALIA] Transaccion id={} descartada: fecha invalida '{}'",
                    input.getId(), input.getFecha());
            return null;
        }

        Transaccion t = new Transaccion();
        t.setIdOrigen(aLong(input.getId()));
        t.setFecha(fecha);
        t.setMonto(monto);
        t.setTipo(tipo);
        return t;
    }

    private Double aDouble(String v) {
        if (v == null || v.isBlank()) return null;
        try { return Double.parseDouble(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Long aLong(String v) {
        if (v == null || v.isBlank()) return null;
        try { return Long.parseLong(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
