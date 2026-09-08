package com.bancoxyz.bff.web.dto;

import com.bancoxyz.bff.core.model.Transaccion;
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

    public static TransaccionWebDTO desde(Transaccion t) {
        return new TransaccionWebDTO(t.getId(), t.getFecha(), t.getMonto(),
                t.getTipo(), t.getDescripcion());
    }
}
