package com.bancoxyz.mstransacciones.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Evento de dominio publicado cuando se crea una transaccion. Es el mensaje
 * que viaja por el topico de Kafka "transacciones" y que consumen los demas
 * microservicios (cuentas y tarjetas) de forma asincrona.
 *
 * Se usa un record para un DTO inmutable, serializado a JSON. La fecha se
 * publica como texto ISO (2026-10-02); sin @JsonFormat Jackson la escribiria
 * como arreglo numerico [2026,10,2], un contrato pobre para otros consumidores.
 */
public record TransaccionCreadaEvent(
        Long transaccionId,
        Long cuentaId,
        Double monto,
        String tipo,
        @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDate fecha) {
}
