package com.bancoxyz.mstransacciones.controller;

import com.bancoxyz.mstransacciones.event.TransaccionCreadaEvent;
import com.bancoxyz.mstransacciones.event.TransaccionEventProducer;
import com.bancoxyz.mstransacciones.model.Transaccion;
import com.bancoxyz.mstransacciones.service.TransaccionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * API del microservicio de Transacciones. Combina las dos capacidades de la
 * experiencia:
 *   - Tolerancia a fallos (Resilience4j) en las consultas.
 *   - Arquitectura de eventos: al registrar una transaccion (POST), publica
 *     un evento en Kafka que consumen ms-cuentas y ms-tarjetas.
 */
@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    private static final Logger log = LoggerFactory.getLogger(TransaccionController.class);
    private final TransaccionService service;
    private final TransaccionEventProducer producer;

    public TransaccionController(TransaccionService service, TransaccionEventProducer producer) {
        this.service = service;
        this.producer = producer;
    }

    @GetMapping
    @CircuitBreaker(name = "transaccionesCB", fallbackMethod = "listarFallback")
    public List<Transaccion> listar() {
        return service.listar();
    }

    /**
     * Registra una transaccion y publica el evento correspondiente. Este es el
     * punto donde nace el flujo asincrono de la arquitectura de eventos.
     */
    @PostMapping
    public ResponseEntity<Transaccion> crear(@RequestBody Transaccion transaccion) {
        if (transaccion.getFecha() == null) {
            transaccion.setFecha(LocalDate.now());
        }
        Transaccion guardada = service.crear(transaccion);

        producer.publicar(new TransaccionCreadaEvent(
                guardada.getId(), guardada.getCuentaId(),
                guardada.getMonto(), guardada.getTipo(), guardada.getFecha()));

        return ResponseEntity.status(201).body(guardada);
    }

    public List<Transaccion> listarFallback(Throwable t) {
        log.warn("[FALLBACK] listar transacciones: {}", t.getMessage());
        return List.of();
    }
}
