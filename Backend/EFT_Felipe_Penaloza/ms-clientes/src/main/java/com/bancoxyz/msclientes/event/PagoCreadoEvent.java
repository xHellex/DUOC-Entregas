package com.bancoxyz.msclientes.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Copia del evento que publica ms-pagos, para que ms-clientes pueda
 * deserializar y consumir los mensajes del topico "pagos".
 */
public record PagoCreadoEvent(
        Long pagoId,
        Long cuentaId,
        Double monto,
        String tipo,
        @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDate fecha) {
}
