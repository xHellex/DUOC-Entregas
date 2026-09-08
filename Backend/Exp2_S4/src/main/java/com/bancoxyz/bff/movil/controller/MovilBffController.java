package com.bancoxyz.bff.movil.controller;

import com.bancoxyz.bff.core.service.BancoService;
import com.bancoxyz.bff.movil.dto.CuentaMovilDTO;
import com.bancoxyz.bff.movil.dto.TransaccionMovilDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BFF MOVIL. Expone los MISMOS datos del banco pero OPTIMIZADOS PARA MOVIL:
 * respuestas ligeras, con solo los campos esenciales, numero de cuenta
 * enmascarado y transacciones recientes limitadas, para reducir el consumo
 * de ancho de banda y mejorar la velocidad en el telefono.
 *
 * Ruta base: /api/movil
 */
@RestController
@RequestMapping("/api/movil")
public class MovilBffController {

    private static final int MAX_TRANSACCIONES_MOVIL = 5;
    private final BancoService banco;

    public MovilBffController(BancoService banco) {
        this.banco = banco;
    }

    /** Lista de cuentas en version ligera. */
    @GetMapping("/cuentas")
    public List<CuentaMovilDTO> listarCuentas() {
        return banco.listarCuentas().stream().map(CuentaMovilDTO::desde).toList();
    }

    /** Resumen ligero de una cuenta. */
    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaMovilDTO> obtenerCuenta(@PathVariable Long id) {
        return banco.obtenerCuenta(id)
                .map(c -> ResponseEntity.ok(CuentaMovilDTO.desde(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Solo las ultimas transacciones, en formato reducido. */
    @GetMapping("/cuentas/{id}/transacciones")
    public List<TransaccionMovilDTO> transaccionesRecientes(@PathVariable Long id) {
        return banco.transaccionesDeCuenta(id).stream()
                .limit(MAX_TRANSACCIONES_MOVIL)
                .map(TransaccionMovilDTO::desde)
                .toList();
    }
}
