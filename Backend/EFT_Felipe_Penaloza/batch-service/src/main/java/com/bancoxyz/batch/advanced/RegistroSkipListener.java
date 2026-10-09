package com.bancoxyz.batch.advanced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

/**
 * SkipListener que registra cada elemento omitido durante la ejecucion,
 * tanto en el log como en un archivo de errores CSV. Proporciona la
 * trazabilidad exigida por la pauta: deja constancia de que se omitio,
 * en que fase (lectura, proceso o escritura) y por que motivo, sin
 * detener el flujo del Job.
 *
 * Es generico para poder reutilizarse en los tres Jobs, que manejan tipos
 * de entrada y salida distintos.
 */
public class RegistroSkipListener implements SkipListener<Object, Object> {

    private static final Logger log = LoggerFactory.getLogger(RegistroSkipListener.class);
    private final String archivoErrores;

    public RegistroSkipListener(String outputDir, String nombreJob) {
        this.archivoErrores = outputDir + "/errores_" + nombreJob + ".csv";
        inicializarArchivo();
    }

    private void inicializarArchivo() {
        try {
            Path dir = Paths.get(archivoErrores).getParent();
            if (dir != null) Files.createDirectories(dir);
            Files.writeString(Paths.get(archivoErrores),
                    "fase,motivo,detalle\n",
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("[SKIP] No se pudo inicializar el archivo de errores: {}", e.getMessage());
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
        String detalle = "";
        if (t instanceof FlatFileParseException ffpe) {
            detalle = ffpe.getInput();
        }
        log.warn("[SKIP-READ] Linea omitida en lectura: {}", detalle);
        escribir("lectura", t.getClass().getSimpleName(), detalle);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        log.warn("[SKIP-PROCESS] Registro omitido en proceso: {} ({})",
                item, t.getMessage());
        escribir("proceso", t.getMessage(), String.valueOf(item));
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        log.warn("[SKIP-WRITE] Registro omitido en escritura: {} ({})",
                item, t.getMessage());
        escribir("escritura", t.getMessage(), String.valueOf(item));
    }

    private synchronized void escribir(String fase, String motivo, String detalle) {
        try {
            String linea = String.format("%s,%s,%s%n",
                    sanitizar(fase), sanitizar(motivo), sanitizar(detalle));
            Files.writeString(Paths.get(archivoErrores), linea, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("[SKIP] No se pudo escribir en el archivo de errores: {}", e.getMessage());
        }
    }

    private String sanitizar(String v) {
        if (v == null) return "";
        String limpio = v.replace("\n", " ").replace("\r", " ").trim();
        if (limpio.contains(",")) limpio = "\"" + limpio.replace("\"", "'") + "\"";
        return limpio;
    }
}
