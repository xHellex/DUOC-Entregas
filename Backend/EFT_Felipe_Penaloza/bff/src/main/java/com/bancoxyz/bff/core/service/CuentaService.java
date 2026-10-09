package com.bancoxyz.bff.core.service;

import com.bancoxyz.bff.core.model.Cuenta;
import com.bancoxyz.bff.core.repository.CuentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio backend de CUENTAS. Fuente de datos independiente encargada de la
 * informacion de cuentas y las operaciones sobre su saldo. Es uno de los tres
 * servicios que los BFF consultan y agregan.
 */
@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public CuentaService(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    public List<Cuenta> listar() {
        return cuentaRepository.findAll();
    }

    public Optional<Cuenta> obtener(Long id) {
        return cuentaRepository.findById(id);
    }

    /** Operacion critica de retiro con validacion de saldo. */
    public Cuenta retirar(Long cuentaId, double monto) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + cuentaId));
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del retiro debe ser positivo");
        }
        if (cuenta.getSaldo() < monto) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        cuenta.setSaldo(cuenta.getSaldo() - monto);
        return cuentaRepository.save(cuenta);
    }
}
