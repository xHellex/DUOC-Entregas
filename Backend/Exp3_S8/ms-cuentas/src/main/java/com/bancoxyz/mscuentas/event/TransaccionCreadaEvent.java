package com.bancoxyz.mscuentas.event;

import java.time.LocalDate;

/**
 * Copia del evento que publica ms-transacciones. Cada microservicio define su
 * propia version del contrato del evento, manteniendo el desacople (no comparten
 * codigo). Debe coincidir en estructura para deserializar el JSON del topico.
 */
public record TransaccionCreadaEvent(
        Long transaccionId,
        Long cuentaId,
        Double monto,
        String tipo,
        LocalDate fecha) {
}
