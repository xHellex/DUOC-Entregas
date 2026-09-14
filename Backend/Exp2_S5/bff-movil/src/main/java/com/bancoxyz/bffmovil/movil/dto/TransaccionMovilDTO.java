package com.bancoxyz.bffmovil.movil.dto;

import com.bancoxyz.bffmovil.client.dto.TransaccionDTO;

/**
 * DTO del BFF Movil: transaccion reducida a lo minimo (fecha corta, monto y
 * tipo). Se omite la descripcion para aligerar la carga de datos en redes
 * moviles.
 */
public record TransaccionMovilDTO(
        String fecha,
        Double monto,
        String tipo) {

    public static TransaccionMovilDTO desde(TransaccionDTO t) {
        return new TransaccionMovilDTO(
                t.fecha() != null ? t.fecha().toString() : null,
                t.monto(), t.tipo());
    }
}
