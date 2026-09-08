package com.bancoxyz.bff.cajero.dto;

/**
 * Respuesta de un retiro en el BFF Cajero: confirma la operacion y devuelve
 * el saldo restante, informacion minima y precisa para el ATM.
 */
public record RetiroResponse(
        boolean exito,
        String mensaje,
        Double saldoRestante) {
}
