package com.bancoxyz.mscuentas.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Copia del evento que publica ms-pagos. Cada microservicio define su
 * propia version del contrato del evento, manteniendo el desacople (no comparten
 * codigo). Debe coincidir en estructura para deserializar el JSON del topico.
 */
public record PagoCreadoEvent(
        Long pagoId,
        Long cuentaId,
        Double monto,
        String tipo,
        @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDate fecha) {
}
