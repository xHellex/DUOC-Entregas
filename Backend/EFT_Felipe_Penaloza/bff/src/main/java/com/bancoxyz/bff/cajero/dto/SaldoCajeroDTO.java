package com.bancoxyz.bff.cajero.dto;

import com.bancoxyz.bff.core.model.Cuenta;

/**
 * DTO del BFF Cajero: respuesta minima para un ATM. Agrega el saldo (servicio
 * de cuentas) con el numero de tarjetas activas (servicio de tarjetas), sin
 * exponer datos personales del titular.
 */
public record SaldoCajeroDTO(
        String cuentaEnmascarada,
        Double saldoDisponible,
        int tarjetasActivas) {

    public static SaldoCajeroDTO desde(Cuenta c, int tarjetasActivas) {
        String num = c.getNumeroCuenta();
        String mask = (num == null || num.length() < 4) ? "****"
                : "**** " + num.substring(num.length() - 4);
        return new SaldoCajeroDTO(mask, c.getSaldo(), tarjetasActivas);
    }
}
