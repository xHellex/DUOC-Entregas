package com.bancoxyz.bff.web.dto;

import com.bancoxyz.bff.core.model.Tarjeta;

/** DTO web de tarjeta: informacion completa, incluido el numero y el cupo. */
public record TarjetaWebDTO(
        Long id, String tipo, String marca, String numeroTarjeta,
        Double cupoDisponible, boolean activa) {

    public static TarjetaWebDTO desde(Tarjeta t) {
        return new TarjetaWebDTO(t.getId(), t.getTipo(), t.getMarca(),
                t.getNumeroTarjeta(), t.getCupoDisponible(), t.isActiva());
    }
}
