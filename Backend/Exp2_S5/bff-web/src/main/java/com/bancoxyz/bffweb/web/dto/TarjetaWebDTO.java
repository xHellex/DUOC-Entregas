package com.bancoxyz.bffweb.web.dto;

import com.bancoxyz.bffweb.client.dto.TarjetaDTO;

/** DTO web de tarjeta: informacion completa, incluido el numero y el cupo. */
public record TarjetaWebDTO(
        Long id, String tipo, String marca, String numeroTarjeta,
        Double cupoDisponible, boolean activa) {

    public static TarjetaWebDTO desde(TarjetaDTO t) {
        return new TarjetaWebDTO(t.id(), t.tipo(), t.marca(),
                t.numeroTarjeta(), t.cupoDisponible(), t.activa());
    }
}
