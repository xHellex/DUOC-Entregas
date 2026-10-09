package com.bancoxyz.bff.web.dto;

import java.util.List;

/**
 * Respuesta AGREGADA del BFF Web. Compone en una sola estructura la
 * informacion proveniente de los TRES servicios backend (cuentas,
 * transacciones y tarjetas), evitando que el frontend web tenga que hacer
 * multiples llamadas. Es el ejemplo principal de agregacion del criterio 4.
 */
public record DashboardWebDTO(
        CuentaWebDTO cuenta,
        List<TransaccionWebDTO> transacciones,
        List<TarjetaWebDTO> tarjetas,
        int totalTransacciones,
        int totalTarjetas) {
}
