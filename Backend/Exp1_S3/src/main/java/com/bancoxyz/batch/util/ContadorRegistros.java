package com.bancoxyz.batch.util;

import org.springframework.core.io.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utilidad para contar los registros de datos de un CSV (excluyendo la
 * cabecera). El total se usa para calcular el tamano de cada particion.
 */
public final class ContadorRegistros {

    private ContadorRegistros() { }

    public static int contar(Resource csv) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csv.getInputStream()))) {
            long lineas = reader.lines().count();
            return (int) Math.max(0, lineas - 1);
        } catch (Exception e) {
            return 0;
        }
    }
}
