package com.bancoxyz.bffweb.web.dto;

import com.bancoxyz.bffweb.client.dto.CuentaDTO;

/**
 * DTO del BFF Web: entrega la informacion COMPLETA de la cuenta, pensada
 * para navegadores con pantalla grande y buena conexion. Incluye todos los
 * campos, con el numero de cuenta completo.
 */
public record CuentaWebDTO(
        Long id,
        String titular,
        String tipo,
        Double saldo,
        String numeroCuenta) {

    public static CuentaWebDTO desde(CuentaDTO c) {
        return new CuentaWebDTO(c.id(), c.titular(), c.tipo(), c.saldo(), c.numeroCuenta());
    }
}
