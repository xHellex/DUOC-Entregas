package com.bancoxyz.bff.web.dto;

import com.bancoxyz.bff.core.model.Cuenta;

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

    public static CuentaWebDTO desde(Cuenta c) {
        return new CuentaWebDTO(c.getId(), c.getTitular(), c.getTipo(),
                c.getSaldo(), c.getNumeroCuenta());
    }
}
