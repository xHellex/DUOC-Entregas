package com.bancoxyz.bffweb.web.dto;

import com.bancoxyz.bffweb.client.dto.TransaccionDTO;
import java.time.LocalDate;

/**
 * DTO del BFF Web: transaccion con todos sus campos, incluida la descripcion
 * y la fecha completa, para vistas de detalle e historial extensos.
 */
public record TransaccionWebDTO(
        Long id,
        LocalDate fecha,
        Double monto,
        String tipo,
        String descripcion) {

    public static TransaccionWebDTO desde(TransaccionDTO t) {
        return new TransaccionWebDTO(t.id(), t.fecha(), t.monto(), t.tipo(), t.descripcion());
    }
}
