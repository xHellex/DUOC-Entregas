package com.bancoxyz.bff.core.service;

import com.bancoxyz.bff.core.model.Cuenta;
import com.bancoxyz.bff.core.model.Transaccion;
import com.bancoxyz.bff.core.repository.CuentaRepository;
import com.bancoxyz.bff.core.repository.TransaccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio central del Banco XYZ. Concentra el acceso a los datos y la
 * logica de negocio, de modo que los tres BFF (web, movil, cajero) consumen
 * la MISMA fuente y solo difieren en como transforman y exponen la respuesta.
 *
 * Este es el nucleo del patron BFF: la logica no se duplica; cada BFF adapta
 * la salida a su cliente.
 */
@Service
public class BancoService {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;

    public BancoService(CuentaRepository cuentaRepository,
                        TransaccionRepository transaccionRepository) {
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public Optional<Cuenta> obtenerCuenta(Long id) {
        return cuentaRepository.findById(id);
    }

    public List<Transaccion> transaccionesDeCuenta(Long cuentaId) {
        return transaccionRepository.findByCuentaIdOrderByFechaDesc(cuentaId);
    }

    /**
     * Aplica un retiro sobre una cuenta (operacion critica usada por el BFF
     * de cajeros). Verifica saldo suficiente antes de descontar.
     */
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
