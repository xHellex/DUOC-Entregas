package com.bancoxyz.mscuentas.controller;

import com.bancoxyz.mscuentas.model.Cuenta;
import com.bancoxyz.mscuentas.service.CuentaService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API del microservicio de Cuentas, protegida por JWT.
 *
 * Aplica tolerancia a fallos con Resilience4j: si el acceso a datos falla,
 * el CircuitBreaker deriva al metodo de fallback en lugar de propagar el
 * error, manteniendo el sistema respondiendo (resiliencia).
 */
@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private static final Logger log = LoggerFactory.getLogger(CuentaController.class);
    private final CuentaService service;

    public CuentaController(CuentaService service) { this.service = service; }

    @GetMapping
    @CircuitBreaker(name = "cuentasCB", fallbackMethod = "listarFallback")
    public List<Cuenta> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @CircuitBreaker(name = "cuentasCB", fallbackMethod = "obtenerFallback")
    public ResponseEntity<Cuenta> obtener(@PathVariable Long id) {
        return service.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ---- Metodos de fallback (respuesta de respaldo ante fallo) ----
    public List<Cuenta> listarFallback(Throwable t) {
        log.warn("[FALLBACK] listar cuentas: {}", t.getMessage());
        return List.of();
    }

    public ResponseEntity<Cuenta> obtenerFallback(Long id, Throwable t) {
        log.warn("[FALLBACK] obtener cuenta {}: {}", id, t.getMessage());
        return ResponseEntity.status(503).build();
    }
}
