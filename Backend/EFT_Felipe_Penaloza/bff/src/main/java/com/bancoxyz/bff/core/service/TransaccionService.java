package com.bancoxyz.bff.core.service;

import com.bancoxyz.bff.core.model.Transaccion;
import com.bancoxyz.bff.core.repository.TransaccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio backend de TRANSACCIONES. Fuente de datos independiente encargada
 * del historial de movimientos de cada cuenta. Es uno de los tres servicios
 * que los BFF consultan y agregan.
 */
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
