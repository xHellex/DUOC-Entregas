package com.bancoxyz.mstarjetas.controller;

import com.bancoxyz.mstarjetas.model.Tarjeta;
import com.bancoxyz.mstarjetas.service.TarjetaService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * API del microservicio de Tarjetas, protegida por JWT y con tolerancia a
 * fallos (Resilience4j + fallback).
 */
@RestController
@RequestMapping("/tarjetas")
public class TarjetaController {

    private static final Logger log = LoggerFactory.getLogger(TarjetaController.class);
    private final TarjetaService service;

    public TarjetaController(TarjetaService service) { this.service = service; }

    @GetMapping
    @CircuitBreaker(name = "tarjetasCB", fallbackMethod = "listarFallback")
    public List<Tarjeta> listar() {
        return service.listar();
    }

    public List<Tarjeta> listarFallback(Throwable t) {
        log.warn("[FALLBACK] listar tarjetas: {}", t.getMessage());
        return List.of();
    }
}
