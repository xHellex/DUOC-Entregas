package com.bancoxyz.bffmovil.client.dto;

/** Forma de una cuenta tal como la entrega banco-servicios por HTTP. */
public record CuentaDTO(
        Long id,
        String titular,
        String tipo,
        Double saldo,
        String numeroCuenta) {
}
