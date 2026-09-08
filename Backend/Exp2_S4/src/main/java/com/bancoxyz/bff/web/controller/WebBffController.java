package com.bancoxyz.bff.web.controller;

import com.bancoxyz.bff.core.service.BancoService;
import com.bancoxyz.bff.web.dto.CuentaWebDTO;
import com.bancoxyz.bff.web.dto.TransaccionWebDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BFF WEB. Expone los datos del banco OPTIMIZADOS PARA NAVEGADORES:
 * respuestas completas, con todos los campos y el historial extenso de
 * transacciones, apto para interfaces complejas de escritorio.
 *
 * Ruta base: /api/web
 */
@RestController
@RequestMapping("/api/web")
public class WebBffController {

    private final BancoService banco;

    public WebBffController(BancoService banco) {
        this.banco = banco;
    }

    /** Lista todas las cuentas con su informacion completa. */
    @GetMapping("/cuentas")
    public List<CuentaWebDTO> listarCuentas() {
        return banco.listarCuentas().stream().map(CuentaWebDTO::desde).toList();
    }

    /** Detalle completo de una cuenta. */
    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaWebDTO> obtenerCuenta(@PathVariable Long id) {
        return banco.obtenerCuenta(id)
                .map(c -> ResponseEntity.ok(CuentaWebDTO.desde(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Historial COMPLETO de transacciones de una cuenta. */
    @GetMapping("/cuentas/{id}/transacciones")
    public List<TransaccionWebDTO> transacciones(@PathVariable Long id) {
        return banco.transaccionesDeCuenta(id).stream()
                .map(TransaccionWebDTO::desde).toList();
    }
}
