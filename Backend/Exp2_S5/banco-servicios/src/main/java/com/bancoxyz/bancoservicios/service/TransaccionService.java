package com.bancoxyz.bancoservicios.service;

import com.bancoxyz.bancoservicios.model.Transaccion;
import com.bancoxyz.bancoservicios.repository.TransaccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    public List<Transaccion> deCuenta(Long cuentaId) {
        return transaccionRepository.findByCuentaIdOrderByFechaDesc(cuentaId);
    }
}
