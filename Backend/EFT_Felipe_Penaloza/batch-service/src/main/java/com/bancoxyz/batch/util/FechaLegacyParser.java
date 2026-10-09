package com.bancoxyz.batch.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilidad para normalizar las fechas del sistema legacy, que llegan en
 * varios formatos inconsistentes:
 *   - yyyy-MM-dd  (formato correcto)
 *   - yyyy/MM/dd
 *   - dd-MM-yyyy
 *   - dd/MM/yyyy
 * Ademas hay fechas semanticamente invalidas como "2024-13-01" (mes 13),
 * que ningun formato acepta y que este parser rechaza devolviendo null.
 */
public final class FechaLegacyParser {

    private static final List<DateTimeFormatter> FORMATOS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );

    private FechaLegacyParser() { }

    /**
     * Intenta convertir el texto a LocalDate probando cada formato conocido.
     * @return la fecha normalizada, o null si el valor es nulo, vacio o
     *         no corresponde a ninguna fecha valida.
     */
    public static LocalDate parsear(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String limpio = valor.trim();
        for (DateTimeFormatter f : FORMATOS) {
            try {
                return LocalDate.parse(limpio, f);
            } catch (Exception ignorada) {
                // se prueba el siguiente formato
            }
        }
        return null;
    }
}
