package com.bancoxyz.msclientes.controller;

import com.bancoxyz.msclientes.model.Cliente;
import com.bancoxyz.msclientes.service.ClienteService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * API del microservicio de Gestion de Clientes, protegida con OAuth2 (Resource
 * Server) y con tolerancia a fallos (Resilience4j + fallback).
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService service;

    public ClienteController(ClienteService service) { this.service = service; }

    @GetMapping
    @CircuitBreaker(name = "clientesCB", fallbackMethod = "listarFallback")
    public List<Cliente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @CircuitBreaker(name = "clientesCB", fallbackMethod = "obtenerFallback")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return service.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public List<Cliente> listarFallback(Throwable t) {
        log.warn("[FALLBACK] listar clientes: {}", t.getMessage());
        return List.of();
    }

    public ResponseEntity<Cliente> obtenerFallback(Long id, Throwable t) {
        log.warn("[FALLBACK] obtener cliente {}: {}", id, t.getMessage());
        return ResponseEntity.status(503).build();
    }
}
