package com.bancoxyz.bancoservicios.service;

import com.bancoxyz.bancoservicios.model.Tarjeta;
import com.bancoxyz.bancoservicios.repository.TarjetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
