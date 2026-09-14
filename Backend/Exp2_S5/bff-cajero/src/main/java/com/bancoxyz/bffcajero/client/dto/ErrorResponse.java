package com.bancoxyz.bffcajero.client.dto;

/** Forma del cuerpo de error que devuelve banco-servicios en 400/409. */
public record ErrorResponse(String mensaje) {
}
