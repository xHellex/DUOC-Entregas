package com.bancoxyz.batch.tasklet;

import com.bancoxyz.batch.model.ResumenTransacciones;
import com.bancoxyz.batch.model.Transaccion;
import com.bancoxyz.batch.repository.ResumenTransaccionesRepository;
import com.bancoxyz.batch.repository.TransaccionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Segundo Step del Job 1. Lee las transacciones ya persistidas, calcula el
 * resumen agregado (totales, montos por tipo, anomalias) y lo escribe en
 * dos destinos: la tabla resumen_transacciones y un archivo CSV.
 *
 * Un Tasklet es el componente adecuado para un paso de consolidacion que
 * no encaja en el patron chunk (reader-processor-writer), porque opera
 * sobre el conjunto completo una sola vez.
 */
public class ResumenTransaccionesTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(ResumenTransaccionesTasklet.class);

    private final TransaccionRepository transaccionRepository;
    private final ResumenTransaccionesRepository resumenRepository;
    private final long totalLeidas;
    private final String outputDir;

    public ResumenTransaccionesTasklet(TransaccionRepository transaccionRepository,
                                       ResumenTransaccionesRepository resumenRepository,
                                       @Value("${app.output.path}") String outputDir,
                                       long totalLeidas) {
        this.transaccionRepository = transaccionRepository;
        this.resumenRepository = resumenRepository;
        this.outputDir = outputDir;
        this.totalLeidas = totalLeidas;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Transaccion> validas = transaccionRepository.findAll();

        long debitos = validas.stream().filter(t -> "debito".equals(t.getTipo())).count();
        long creditos = validas.stream().filter(t -> "credito".equals(t.getTipo())).count();
        double montoDebitos = validas.stream().filter(t -> "debito".equals(t.getTipo()))
                .mapToDouble(Transaccion::getMonto).sum();
        double montoCreditos = validas.stream().filter(t -> "credito".equals(t.getTipo()))
                .mapToDouble(Transaccion::getMonto).sum();

        ResumenTransacciones resumen = new ResumenTransacciones();
        resumen.setFechaEjecucion(LocalDateTime.now());
        resumen.setTotalLeidas(totalLeidas);
        resumen.setTotalValidas(validas.size());
        resumen.setTotalAnomalias(totalLeidas - validas.size());
        resumen.setCantidadDebitos(debitos);
        resumen.setCantidadCreditos(creditos);
        resumen.setMontoTotalDebitos(redondear(montoDebitos));
        resumen.setMontoTotalCreditos(redondear(montoCreditos));

        resumenRepository.save(resumen);
        escribirCsv(resumen);

        log.info("[RESUMEN] Transacciones validas={} anomalias={} debitos={} creditos={}",
                resumen.getTotalValidas(), resumen.getTotalAnomalias(), debitos, creditos);
        return RepeatStatus.FINISHED;
    }

    private void escribirCsv(ResumenTransacciones r) throws IOException {
        Path dir = Paths.get(outputDir);
        Files.createDirectories(dir);
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path archivo = dir.resolve("resumen_transacciones_" + ts + ".csv");
        StringBuilder sb = new StringBuilder();
        sb.append("metrica,valor\n");
        sb.append("fecha_ejecucion,").append(r.getFechaEjecucion()).append("\n");
        sb.append("total_leidas,").append(r.getTotalLeidas()).append("\n");
        sb.append("total_validas,").append(r.getTotalValidas()).append("\n");
        sb.append("total_anomalias,").append(r.getTotalAnomalias()).append("\n");
        sb.append("cantidad_debitos,").append(r.getCantidadDebitos()).append("\n");
        sb.append("cantidad_creditos,").append(r.getCantidadCreditos()).append("\n");
        sb.append("monto_total_debitos,").append(r.getMontoTotalDebitos()).append("\n");
        sb.append("monto_total_creditos,").append(r.getMontoTotalCreditos()).append("\n");
        Files.writeString(archivo, sb.toString());
        log.info("[RESUMEN] CSV generado: {}", archivo.toAbsolutePath());
    }

    private double redondear(double v) { return Math.round(v * 100.0) / 100.0; }
}
