package com.bancoxyz.bffcajero.cajero.dto;

import com.bancoxyz.bffcajero.client.dto.CuentaDTO;

/**
 * DTO del BFF Cajero: respuesta minima para un ATM. Agrega el saldo con el
 * numero de tarjetas activas (dos llamadas HTTP independientes a
 * banco-servicios), sin exponer datos personales del titular.
 */
public record SaldoCajeroDTO(
        String cuentaEnmascarada,
        Double saldoDisponible,
        int tarjetasActivas) {

    public static SaldoCajeroDTO desde(CuentaDTO c, int tarjetasActivas) {
        String num = c.numeroCuenta();
        String mask = (num == null || num.length() < 4) ? "****"
                : "**** " + num.substring(num.length() - 4);
        return new SaldoCajeroDTO(mask, c.saldo(), tarjetasActivas);
    }
}
