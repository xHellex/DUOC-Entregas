package com.bancoxyz.mstarjetas.event;

import java.time.LocalDate;

/**
 * Copia del evento que publica ms-transacciones, para que ms-tarjetas pueda
 * deserializar y consumir los mensajes del topico "transacciones".
 */
public record TransaccionCreadaEvent(
        Long transaccionId,
        Long cuentaId,
        Double monto,
        String tipo,
        LocalDate fecha) {
}
