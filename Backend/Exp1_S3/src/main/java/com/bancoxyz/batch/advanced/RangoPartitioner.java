package com.bancoxyz.batch.advanced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Particionador que divide el conjunto total de registros en particiones
 * de rangos contiguos (start-end), siguiendo el patron de particionamiento
 * de Spring Batch.
 *
 * Cada particion recibe un ExecutionContext con las claves "start" y "end"
 * que delimitan el subconjunto de lineas que debe procesar. El ItemReader
 * de cada minionStep lee esas claves para saltar hasta su rango y leer solo
 * su porcion, de modo que las particiones no se solapan.
 *
 * El numero de particiones lo define gridSize (parametro de la actividad).
 */
public class RangoPartitioner implements Partitioner {

    private static final Logger log = LoggerFactory.getLogger(RangoPartitioner.class);
    private final int totalRegistros;

    public RangoPartitioner(int totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> particiones = new HashMap<>();
        int tamano = (int) Math.ceil((double) totalRegistros / gridSize);
        int start = 0;

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext contexto = new ExecutionContext();
            int end = Math.min(start + tamano - 1, totalRegistros - 1);

            contexto.putInt("start", start);
            contexto.putInt("end", end);
            contexto.putString("nombreParticion", "particion" + i);
            particiones.put("particion" + i, contexto);

            log.info("[PARTICION] particion{} -> registros {} a {}", i, start, end);
            start = end + 1;
            if (start >= totalRegistros) break;
        }
        return particiones;
    }
}
