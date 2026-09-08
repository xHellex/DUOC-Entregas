package com.bancoxyz.bff.cajero.dto;

import com.bancoxyz.bff.core.model.Cuenta;

/**
 * DTO del BFF Cajero: respuesta minima para una consulta de saldo en un ATM.
 * Solo expone el numero de cuenta enmascarado y el saldo disponible, sin
 * datos personales del titular (privacidad en un espacio publico) ni otros
 * campos innecesarios para la operacion.
 */
public record SaldoCajeroDTO(
        String cuentaEnmascarada,
        Double saldoDisponible) {

    public static SaldoCajeroDTO desde(Cuenta c) {
        String num = c.getNumeroCuenta();
        String mask = (num == null || num.length() < 4) ? "****"
                : "**** " + num.substring(num.length() - 4);
        return new SaldoCajeroDTO(mask, c.getSaldo());
    }
}
