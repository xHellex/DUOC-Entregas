package com.bancoxyz.mspagos.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Productor de eventos. Publica un PagoCreadoEvent en el topico
 * "pagos" cada vez que se registra una pago. Los consumidores
 * (ms-cuentas, ms-clientes) reaccionan de forma asincrona y desacoplada:
 * ms-pagos no sabe ni le importa quien consume el evento.
 */
@Component
public class PagoEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PagoEventProducer.class);
    public static final String TOPICO = "pagos";

    private final KafkaTemplate<String, PagoCreadoEvent> kafkaTemplate;

    public PagoEventProducer(KafkaTemplate<String, PagoCreadoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(PagoCreadoEvent evento) {
        log.info("[KAFKA-PRODUCER] Publicando evento en topico '{}': {}", TOPICO, evento);
        kafkaTemplate.send(TOPICO, String.valueOf(evento.cuentaId()), evento);
    }
}
