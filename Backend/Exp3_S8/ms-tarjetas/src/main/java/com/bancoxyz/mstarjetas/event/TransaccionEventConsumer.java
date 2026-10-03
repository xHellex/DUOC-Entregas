package com.bancoxyz.mstarjetas.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos en ms-tarjetas. Escucha el topico "transacciones" y
 * registra el movimiento asociado a la cuenta. Demuestra que un mismo evento
 * es consumido por MULTIPLES microservicios de forma independiente (cada uno
 * con su propio groupId), que es la esencia del patron publish/subscribe.
 */
@Component
public class TransaccionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransaccionEventConsumer.class);

    @KafkaListener(topics = "transacciones", groupId = "grupo-tarjetas")
    public void consumir(TransaccionCreadaEvent evento) {
        log.info("[KAFKA-CONSUMER tarjetas] Evento recibido: {}", evento);
        log.info("[KAFKA-CONSUMER tarjetas] Registrando movimiento de {} por {} en cuenta {}",
                evento.tipo(), evento.monto(), evento.cuentaId());
    }
}
