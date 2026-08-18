package com.bancoxyz.batch.processor;

import com.bancoxyz.batch.model.CuentaInteres;
import com.bancoxyz.batch.model.InteresInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

/**
 * Calcula el interes mensual de cada cuenta y su saldo final (Job 2).
 *
 * Tasas mensuales por tipo de cuenta (valores academicos de ejemplo):
 *   - ahorro    : +0,5 %  (el banco paga interes al cliente)
 *   - prestamo  : +1,5 %  (el cliente paga interes sobre la deuda)
 *   - hipoteca  : +0,9 %
 *
 * Reglas de anomalia:
 *   - saldo vacio, no numerico o <= 0  -> se omite (no tiene sentido
 *     calcular interes sobre saldo cero, ej. Alice Brown)
 *   - edad fuera de rango [18, 75]     -> se omite (ej. Steve Rogers, 80)
 *   - tipo desconocido                 -> se omite
 */
public class InteresProcessor implements ItemProcessor<InteresInput, CuentaInteres> {

    private static final Logger log = LoggerFactory.getLogger(InteresProcessor.class);

    private static final Map<String, Double> TASAS = Map.of(
            "ahorro", 0.005,
            "prestamo", 0.015,
            "hipoteca", 0.009
    );
    private static final int EDAD_MIN = 18;
    private static final int EDAD_MAX = 75;

    @Override
    public CuentaInteres process(InteresInput input) {
        // Validacion de saldo
        Double saldo = aDouble(input.getSaldo());
        if (saldo == null || saldo <= 0) {
            log.warn("[ANOMALIA] Cuenta id={} descartada: saldo invalido '{}'",
                    input.getCuentaId(), input.getSaldo());
            return null;
        }

        // Validacion de edad
        Integer edad = aInt(input.getEdad());
        if (edad == null || edad < EDAD_MIN || edad > EDAD_MAX) {
            log.warn("[ANOMALIA] Cuenta id={} descartada: edad fuera de rango '{}'",
                    input.getCuentaId(), input.getEdad());
            return null;
        }

        // Validacion de tipo
        String tipo = input.getTipo() == null ? "" : input.getTipo().trim().toLowerCase();
        Double tasa = TASAS.get(tipo);
        if (tasa == null) {
            log.warn("[ANOMALIA] Cuenta id={} descartada: tipo desconocido '{}'",
                    input.getCuentaId(), input.getTipo());
            return null;
        }

        double interes = saldo * tasa;
        double saldoFinal = saldo + interes;

        CuentaInteres c = new CuentaInteres();
        c.setCuentaId(aLong(input.getCuentaId()));
        c.setNombre(input.getNombre());
        c.setSaldoInicial(saldo);
        c.setEdad(edad);
        c.setTipo(tipo);
        c.setTasaAplicada(tasa);
        c.setInteresGenerado(redondear(interes));
        c.setSaldoFinal(redondear(saldoFinal));
        return c;
    }

    private double redondear(double v) { return Math.round(v * 100.0) / 100.0; }

    private Double aDouble(String v) {
        if (v == null || v.isBlank()) return null;
        try { return Double.parseDouble(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Integer aInt(String v) {
        if (v == null || v.isBlank()) return null;
        try { return Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Long aLong(String v) {
        if (v == null || v.isBlank()) return null;
        try { return Long.parseLong(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
