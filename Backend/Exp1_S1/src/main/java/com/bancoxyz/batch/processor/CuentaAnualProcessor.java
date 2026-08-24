package com.bancoxyz.batch.processor;

import com.bancoxyz.batch.model.CuentaAnual;
import com.bancoxyz.batch.model.CuentaAnualInput;
import com.bancoxyz.batch.util.FechaLegacyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

/**
 * Compila y normaliza las operaciones anuales para los estados de cuenta
 * de auditoria (Job 3).
 *
 * A diferencia de los otros jobs, aqui se prioriza NO perder registros de
 * auditoria: en lugar de descartar por descripcion faltante, se completa
 * con un valor por defecto. Solo se descartan filas sin dato utilizable:
 *   - fecha invalida        -> se omite (no ubicable en el tiempo)
 *   - monto vacio/no numero -> se omite
 * El monto negativo SI se conserva: representa retiros y compras legitimas.
 */
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnualInput, CuentaAnual> {

    private static final Logger log = LoggerFactory.getLogger(CuentaAnualProcessor.class);
    private static final String DESCRIPCION_DEFAULT = "Sin descripcion";

    @Override
    public CuentaAnual process(CuentaAnualInput input) {
        LocalDate fecha = FechaLegacyParser.parsear(input.getFecha());
        if (fecha == null) {
            log.warn("[ANOMALIA] Operacion cuenta={} descartada: fecha invalida '{}'",
                    input.getCuentaId(), input.getFecha());
            return null;
        }

        Double monto = aDouble(input.getMonto());
        if (monto == null) {
            log.warn("[ANOMALIA] Operacion cuenta={} descartada: monto invalido '{}'",
                    input.getCuentaId(), input.getMonto());
            return null;
        }

        String descripcion = input.getDescripcion();
        if (descripcion == null || descripcion.isBlank()) {
            descripcion = DESCRIPCION_DEFAULT;
        }

        CuentaAnual c = new CuentaAnual();
        c.setCuentaId(aLong(input.getCuentaId()));
        c.setFecha(fecha);
        c.setTransaccion(input.getTransaccion());
        c.setMonto(monto);
        c.setDescripcion(descripcion.trim());
        return c;
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
