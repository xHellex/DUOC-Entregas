package com.bancoxyz.msclientes.event;

import com.bancoxyz.msclientes.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos en ms-clientes. Escucha el topico "pagos" (el mismo que
 * consume ms-cuentas, pero con su propio groupId) y notifica al cliente dueno
 * de la cuenta afectada. Demuestra el patron publish/subscribe: un mismo evento
 * es procesado por multiples microservicios de forma independiente, y habilita
 * casos como notificaciones y alertas de seguridad al cliente.
 */
@Component
public class PagoEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PagoEventConsumer.class);
    private final ClienteRepository clienteRepository;

    public PagoEventConsumer(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @KafkaListener(topics = "pagos", groupId = "grupo-clientes")
    public void consumir(PagoCreadoEvent evento) {
        log.info("[KAFKA-CONSUMER clientes] Evento recibido: {}", evento);
        clienteRepository.findByCuentaId(evento.cuentaId()).ifPresentOrElse(cliente ->
            log.info("[KAFKA-CONSUMER clientes] Notificando a {} ({}): movimiento {} por {} en su cuenta {}",
                    cliente.getNombre(), cliente.getEmail(), evento.tipo(), evento.monto(), evento.cuentaId()),
            () -> log.warn("[KAFKA-CONSUMER clientes] Sin cliente asociado a la cuenta {}", evento.cuentaId()));
    }
}
