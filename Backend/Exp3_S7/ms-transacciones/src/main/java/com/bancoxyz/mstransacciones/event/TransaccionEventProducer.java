package com.bancoxyz.mstransacciones.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Productor de eventos. Publica un TransaccionCreadaEvent en el topico
 * "transacciones" cada vez que se registra una transaccion. Los consumidores
 * (ms-cuentas, ms-tarjetas) reaccionan de forma asincrona y desacoplada:
 * ms-transacciones no sabe ni le importa quien consume el evento.
 */
@Component
public class TransaccionEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TransaccionEventProducer.class);
    public static final String TOPICO = "transacciones";

    private final KafkaTemplate<String, TransaccionCreadaEvent> kafkaTemplate;

    public TransaccionEventProducer(KafkaTemplate<String, TransaccionCreadaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(TransaccionCreadaEvent evento) {
        log.info("[KAFKA-PRODUCER] Publicando evento en topico '{}': {}", TOPICO, evento);
        kafkaTemplate.send(TOPICO, String.valueOf(evento.cuentaId()), evento);
    }
}
