package com.bancoxyz.bff.cajero.dto;

import jakarta.validation.constraints.Positive;

/**
 * Peticion de retiro en el BFF Cajero. El monto se valida como positivo
 * antes de procesar la operacion critica.
 */
public record RetiroRequest(
        @Positive(message = "El monto debe ser positivo") double monto) {
}
