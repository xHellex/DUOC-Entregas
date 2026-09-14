package com.bancoxyz.bffmovil.movil.dto;

import com.bancoxyz.bffmovil.client.dto.CuentaDTO;

/**
 * DTO del BFF Movil: version LIGERA de la cuenta. Solo los campos esenciales
 * para mostrar en una pantalla pequena (titular y saldo), y el numero de
 * cuenta ENMASCARADO para reducir datos sensibles y ancho de banda.
 */
public record CuentaMovilDTO(
        Long id,
        String titular,
        Double saldo,
        String cuentaEnmascarada) {

    public static CuentaMovilDTO desde(CuentaDTO c) {
        return new CuentaMovilDTO(c.id(), c.titular(), c.saldo(), enmascarar(c.numeroCuenta()));
    }

    private static String enmascarar(String numero) {
        if (numero == null || numero.length() < 4) return "****";
        return "**** " + numero.substring(numero.length() - 4);
    }
}
