package com.bancoxyz.mspagos.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Evento de dominio publicado cuando se crea una pago. Es el mensaje
 * que viaja por el topico de Kafka "pagos" y que consumen los demas
 * microservicios (cuentas y clientes) de forma asincrona.
 *
 * Se usa un record para un DTO inmutable, serializado a JSON. La fecha se
 * publica como texto ISO; sin @JsonFormat Jackson la escribiria como arreglo
 * numerico [aaaa,mm,dd], un contrato pobre para otros consumidores.
 */
public record PagoCreadoEvent(
        Long pagoId,
        Long cuentaId,
        Double monto,
        String tipo,
        @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDate fecha) {
}
