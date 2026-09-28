package com.bancoxyz.mstarjetas.event;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contrato del consumidor: debe leer el JSON del topico en su PROPIA clase de
 * evento, ignorando el header __TypeId__ que apunte a una clase de otro
 * microservicio. Replica config-repo/ms-tarjetas.yml.
 */
class TransaccionCreadaEventContractTest {

    private JsonDeserializer<TransaccionCreadaEvent> deserializador() {
        JsonDeserializer<TransaccionCreadaEvent> d = new JsonDeserializer<>();
        d.configure(Map.of(
                "spring.json.use.type.headers", false,
                "spring.json.trusted.packages", "com.bancoxyz.mstarjetas.event",
                "spring.json.value.default.type", "com.bancoxyz.mstarjetas.event.TransaccionCreadaEvent"), false);
        return d;
    }

    private static final byte[] JSON =
            "{\"transaccionId\":4,\"cuentaId\":101,\"monto\":500.0,\"tipo\":\"credito\",\"fecha\":\"2026-09-24\"}"
                    .getBytes(StandardCharsets.UTF_8);

    @Test
    void leeElEventoDesdeJsonPuro() {
        TransaccionCreadaEvent e = deserializador().deserialize("transacciones", new RecordHeaders(), JSON);
        assertEquals(new TransaccionCreadaEvent(4L, 101L, 500.0, "credito", LocalDate.of(2026, 9, 24)), e);
    }

    @Test
    void ignoraElHeaderTypeIdDeUnaClaseAjena() {
        RecordHeaders headers = new RecordHeaders();
        headers.add("__TypeId__",
                "com.bancoxyz.mstransacciones.event.TransaccionCreadaEvent".getBytes(StandardCharsets.UTF_8));
        TransaccionCreadaEvent e = deserializador().deserialize("transacciones", headers, JSON);
        assertEquals(101L, e.cuentaId());
    }
}
