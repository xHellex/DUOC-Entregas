package com.bancoxyz.bffmovil.movil.dto;

import java.util.List;

/**
 * Respuesta AGREGADA y LIGERA del BFF Movil. Compone datos obtenidos de
 * banco-servicios (cuenta + tarjetas + transacciones, tres llamadas HTTP
 * independientes) reducidos al minimo util para el movil: saldo de la
 * cuenta, cantidad de tarjetas activas y solo las ultimas transacciones.
 */
public record ResumenMovilDTO(
        String titular,
        Double saldo,
        String cuentaEnmascarada,
        int tarjetasActivas,
        List<TransaccionMovilDTO> ultimasTransacciones) {
}
