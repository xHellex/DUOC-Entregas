package com.bancoxyz.bff.movil.dto;

import com.bancoxyz.bff.core.model.Transaccion;

/**
 * DTO del BFF Movil: transaccion reducida a lo minimo (fecha corta, monto y
 * tipo). Se omite la descripcion para aligerar la carga de datos en redes
 * moviles.
 */
public record TransaccionMovilDTO(
        String fecha,
        Double monto,
        String tipo) {

    public static TransaccionMovilDTO desde(Transaccion t) {
        return new TransaccionMovilDTO(
                t.getFecha() != null ? t.getFecha().toString() : null,
                t.getMonto(), t.getTipo());
    }
}
