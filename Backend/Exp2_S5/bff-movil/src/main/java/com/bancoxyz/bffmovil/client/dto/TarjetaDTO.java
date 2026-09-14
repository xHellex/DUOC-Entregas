package com.bancoxyz.bffmovil.client.dto;

/** Forma de una tarjeta tal como la entrega banco-servicios por HTTP. */
public record TarjetaDTO(
        Long id,
        Long cuentaId,
        String tipo,
        String marca,
        String numeroTarjeta,
        Double cupoDisponible,
        boolean activa) {
}
