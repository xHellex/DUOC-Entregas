package com.bancoxyz.mspagos.controller;

import com.bancoxyz.mspagos.event.PagoCreadoEvent;
import com.bancoxyz.mspagos.event.PagoEventProducer;
import com.bancoxyz.mspagos.model.Pago;
import com.bancoxyz.mspagos.service.PagoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * API del microservicio de Pagos. Combina las dos capacidades de la
 * experiencia:
 *   - Tolerancia a fallos (Resilience4j) en las consultas.
 *   - Arquitectura de eventos: al registrar una pago (POST), publica
 *     un evento en Kafka que consumen ms-cuentas y ms-clientes.
 */
@RestController
@RequestMapping("/pagos")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);
    private final PagoService service;
    private final PagoEventProducer producer;

    public PagoController(PagoService service, PagoEventProducer producer) {
        this.service = service;
        this.producer = producer;
    }

    @GetMapping
    @CircuitBreaker(name = "pagosCB", fallbackMethod = "listarFallback")
    public List<Pago> listar() {
        return service.listar();
    }

    /**
     * Registra una pago y publica el evento correspondiente. Este es el
     * punto donde nace el flujo asincrono de la arquitectura de eventos.
     */
    @PostMapping
    public ResponseEntity<Pago> crear(@RequestBody Pago pago) {
        if (pago.getFecha() == null) {
            pago.setFecha(LocalDate.now());
        }
        Pago guardada = service.crear(pago);

        producer.publicar(new PagoCreadoEvent(
                guardada.getId(), guardada.getCuentaId(),
                guardada.getMonto(), guardada.getTipo(), guardada.getFecha()));

        return ResponseEntity.status(201).body(guardada);
    }

    public List<Pago> listarFallback(Throwable t) {
        log.warn("[FALLBACK] listar pagos: {}", t.getMessage());
        return List.of();
    }
}
