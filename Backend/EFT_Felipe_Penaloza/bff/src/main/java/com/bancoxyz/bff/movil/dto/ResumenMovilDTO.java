package com.bancoxyz.bff.movil.dto;

import java.util.List;

/**
 * Respuesta AGREGADA y LIGERA del BFF Movil. Compone datos de los tres
 * servicios backend pero reducidos al minimo util para el movil: saldo de la
 * cuenta, cantidad de tarjetas activas y solo las ultimas transacciones.
 * Demuestra agregacion adaptada al canal (no entrega el detalle completo).
 */
public record ResumenMovilDTO(
        String titular,
        Double saldo,
        String cuentaEnmascarada,
        int tarjetasActivas,
        List<TransaccionMovilDTO> ultimasTransacciones) {
}
