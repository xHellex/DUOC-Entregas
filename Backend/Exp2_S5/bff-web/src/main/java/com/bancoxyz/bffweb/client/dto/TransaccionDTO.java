package com.bancoxyz.bffweb.client.dto;

import java.time.LocalDate;

/** Forma de una transaccion tal como la entrega banco-servicios por HTTP. */
public record TransaccionDTO(
        Long id,
        Long cuentaId,
        LocalDate fecha,
        Double monto,
        String tipo,
        String descripcion) {
}
