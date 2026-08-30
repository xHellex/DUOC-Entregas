package com.bancoxyz.batch.tasklet;

import com.bancoxyz.batch.model.CuentaAnual;
import com.bancoxyz.batch.model.EstadoCuentaAnual;
import com.bancoxyz.batch.repository.CuentaAnualRepository;
import com.bancoxyz.batch.repository.EstadoCuentaAnualRepository;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Segundo Step del Job 3. Compila las operaciones anuales normalizadas
 * agrupandolas por cuenta, y genera el informe detallado para auditorias
 * en dos destinos: la tabla estado_cuenta_anual y un archivo CSV.
 *
 * Por cada cuenta consolida: numero de movimientos, total de ingresos
 * (montos positivos), total de egresos (montos negativos) y saldo neto.
 */
public class InformeAnualTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(InformeAnualTasklet.class);

    private final CuentaAnualRepository cuentaAnualRepository;
    private final EstadoCuentaAnualRepository estadoRepository;
    private final String outputDir;

    public InformeAnualTasklet(CuentaAnualRepository cuentaAnualRepository,
                               EstadoCuentaAnualRepository estadoRepository,
                               @Value("${app.output.path}") String outputDir) {
        this.cuentaAnualRepository = cuentaAnualRepository;
        this.estadoRepository = estadoRepository;
        this.outputDir = outputDir;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<CuentaAnual> operaciones = cuentaAnualRepository.findAll();

        Map<Long, List<CuentaAnual>> porCuenta = operaciones.stream()
                .filter(o -> o.getCuentaId() != null)
                .collect(Collectors.groupingBy(CuentaAnual::getCuentaId, TreeMap::new, Collectors.toList()));

        StringBuilder sb = new StringBuilder();
        sb.append("cuenta_id,cantidad_movimientos,total_ingresos,total_egresos,saldo_neto\n");

        for (Map.Entry<Long, List<CuentaAnual>> e : porCuenta.entrySet()) {
            List<CuentaAnual> movs = e.getValue();
            double ingresos = movs.stream().mapToDouble(CuentaAnual::getMonto).filter(m -> m > 0).sum();
            double egresos = movs.stream().mapToDouble(CuentaAnual::getMonto).filter(m -> m < 0).sum();
            double neto = ingresos + egresos;

            EstadoCuentaAnual estado = new EstadoCuentaAnual();
            estado.setCuentaId(e.getKey());
            estado.setCantidadMovimientos(movs.size());
            estado.setTotalIngresos(redondear(ingresos));
            estado.setTotalEgresos(redondear(egresos));
            estado.setSaldoNeto(redondear(neto));
            estadoRepository.save(estado);

            sb.append(e.getKey()).append(",")
              .append(movs.size()).append(",")
              .append(redondear(ingresos)).append(",")
              .append(redondear(egresos)).append(",")
              .append(redondear(neto)).append("\n");
        }

        escribirCsv(sb.toString());
        log.info("[INFORME] Estados de cuenta generados para {} cuentas", porCuenta.size());
        return RepeatStatus.FINISHED;
    }

    private void escribirCsv(String contenido) throws IOException {
        Path dir = Paths.get(outputDir);
        Files.createDirectories(dir);
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path archivo = dir.resolve("informe_anual_" + ts + ".csv");
        Files.writeString(archivo, contenido);
        log.info("[INFORME] CSV generado: {}", archivo.toAbsolutePath());
    }

    private double redondear(double v) { return Math.round(v * 100.0) / 100.0; }
}
