package com.bancoxyz.bff.core.service;

import com.bancoxyz.bff.core.model.Tarjeta;
import com.bancoxyz.bff.core.repository.TarjetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio backend de TARJETAS. Fuente de datos independiente encargada de
 * las tarjetas asociadas a una cuenta. Es uno de los tres servicios que los
 * BFF consultan y agregan para construir respuestas compuestas.
 */
@Service
public class TarjetaService {

    private final TarjetaRepository tarjetaRepository;

    public TarjetaService(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }

    public List<Tarjeta> deCuenta(Long cuentaId) {
        return tarjetaRepository.findByCuentaId(cuentaId);
    }
}
