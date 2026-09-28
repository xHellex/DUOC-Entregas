package com.bancoxyz.mscuentas.event;

import com.bancoxyz.mscuentas.model.Cuenta;
import com.bancoxyz.mscuentas.repository.CuentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos en ms-cuentas. Escucha el topico "transacciones" y,
 * al recibir un TransaccionCreadaEvent, actualiza el saldo de la cuenta
 * afectada (un debito resta, un credito suma). Reacciona de forma asincrona
 * a lo que publica ms-transacciones, sin acoplamiento directo entre ambos.
 */
@Component
public class TransaccionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransaccionEventConsumer.class);
    private final CuentaRepository cuentaRepository;

    public TransaccionEventConsumer(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @KafkaListener(topics = "transacciones", groupId = "grupo-cuentas")
    public void consumir(TransaccionCreadaEvent evento) {
        log.info("[KAFKA-CONSUMER cuentas] Evento recibido: {}", evento);
        cuentaRepository.findById(evento.cuentaId()).ifPresentOrElse(cuenta -> {
            double delta = "credito".equalsIgnoreCase(evento.tipo())
                    ? evento.monto() : -evento.monto();
            cuenta.setSaldo(cuenta.getSaldo() + delta);
            cuentaRepository.save(cuenta);
            log.info("[KAFKA-CONSUMER cuentas] Saldo actualizado para cuenta {}: nuevo saldo {}",
                    cuenta.getId(), cuenta.getSaldo());
        }, () -> log.warn("[KAFKA-CONSUMER cuentas] Cuenta {} no encontrada", evento.cuentaId()));
    }
}
