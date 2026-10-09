package com.bancoxyz.bff.movil.dto;

import com.bancoxyz.bff.core.model.Cuenta;

/**
 * DTO del BFF Movil: version LIGERA de la cuenta. Solo los campos esenciales
 * para mostrar en una pantalla pequena (titular y saldo), y el numero de
 * cuenta ENMASCARADO para reducir datos sensibles y ancho de banda.
 *
 * Se omiten campos como el tipo de cuenta, que no son imprescindibles en la
 * vista movil rapida.
 */
public record CuentaMovilDTO(
        Long id,
        String titular,
        Double saldo,
        String cuentaEnmascarada) {

    public static CuentaMovilDTO desde(Cuenta c) {
        return new CuentaMovilDTO(c.getId(), c.getTitular(), c.getSaldo(),
                enmascarar(c.getNumeroCuenta()));
    }

    private static String enmascarar(String numero) {
        if (numero == null || numero.length() < 4) return "****";
        return "**** " + numero.substring(numero.length() - 4);
    }
}
