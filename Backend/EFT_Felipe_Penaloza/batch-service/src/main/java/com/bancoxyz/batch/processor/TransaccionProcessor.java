package com.bancoxyz.batch.processor;

import com.bancoxyz.batch.advanced.InvalidDataException;
import com.bancoxyz.batch.model.Transaccion;
import com.bancoxyz.batch.model.TransaccionInput;
import com.bancoxyz.batch.util.FechaLegacyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.util.Set;

/**
 * Procesa cada transaccion cruda del CSV y decide si es valida (Job 1).
 *
 * Reglas de negocio para detectar anomalias:
 *   - monto vacio, no numerico, negativo o cero
 *   - tipo distinto de "debito" o "credito" (el legacy trae "invalid")
 *   - fecha nula o invalida, ej. 2024-13-01
 *
 * A diferencia de la version de la semana 1 (que devolvia null para filtrar
 * en silencio), aqui las anomalias lanzan InvalidDataException. Esto activa
 * la SkipPolicy y el SkipListener, de modo que cada omision queda registrada
 * en el log y en el archivo de errores, cumpliendo el manejo de errores con
 * politicas y listeners exigido por la evaluacion.
 */
public class TransaccionProcessor implements ItemProcessor<TransaccionInput, Transaccion> {

    private static final Logger log = LoggerFactory.getLogger(TransaccionProcessor.class);
    private static final Set<String> TIPOS_VALIDOS = Set.of("debito", "credito");

    @Override
    public Transaccion process(TransaccionInput input) {
        Double monto = aDouble(input.getMonto());
        if (monto == null || monto <= 0) {
            throw new InvalidDataException(
                    "Monto invalido en transaccion id=" + input.getId() + ": '" + input.getMonto() + "'");
        }

        String tipo = input.getTipo() == null ? "" : input.getTipo().trim().toLowerCase();
        if (!TIPOS_VALIDOS.contains(tipo)) {
            throw new InvalidDataException(
                    "Tipo invalido en transaccion id=" + input.getId() + ": '" + input.getTipo() + "'");
        }

        LocalDate fecha = FechaLegacyParser.parsear(input.getFecha());
        if (fecha == null) {
            throw new InvalidDataException(
                    "Fecha invalida en transaccion id=" + input.getId() + ": '" + input.getFecha() + "'");
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
