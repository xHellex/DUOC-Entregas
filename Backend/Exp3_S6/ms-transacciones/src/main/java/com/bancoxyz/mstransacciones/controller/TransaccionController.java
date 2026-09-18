package com.bancoxyz.mstransacciones.controller;

import com.bancoxyz.mstransacciones.model.Transaccion;
import com.bancoxyz.mstransacciones.service.TransaccionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * API del microservicio de Transacciones, protegida por JWT y con tolerancia
 * a fallos (Resilience4j + fallback).
 */
@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    private static final Logger log = LoggerFactory.getLogger(TransaccionController.class);
    private final TransaccionService service;

    public TransaccionController(TransaccionService service) { this.service = service; }

    @GetMapping
    @CircuitBreaker(name = "transaccionesCB", fallbackMethod = "listarFallback")
    public List<Transaccion> listar() {
        return service.listar();
    }

    public List<Transaccion> listarFallback(Throwable t) {
        log.warn("[FALLBACK] listar transacciones: {}", t.getMessage());
        return List.of();
    }
}
