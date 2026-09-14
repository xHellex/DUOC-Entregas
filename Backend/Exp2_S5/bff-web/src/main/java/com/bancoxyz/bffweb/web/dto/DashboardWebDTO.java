package com.bancoxyz.bffweb.web.dto;

import java.util.List;

/**
 * Respuesta AGREGADA del BFF Web. Compone en una sola estructura la
 * informacion proveniente de banco-servicios (cuenta, transacciones y
 * tarjetas obtenidas con tres llamadas HTTP independientes), evitando que
 * el frontend web tenga que hacer multiples llamadas.
 */
public record DashboardWebDTO(
        CuentaWebDTO cuenta,
        List<TransaccionWebDTO> transacciones,
        List<TarjetaWebDTO> tarjetas,
        int totalTransacciones,
        int totalTarjetas) {
}
