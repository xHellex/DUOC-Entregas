package com.bancoxyz.mstransacciones.event;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Contrato del mensaje que viaja por Kafka: el evento debe ser JSON puro,
 * con la fecha en formato ISO y sin el header __TypeId__ con el nombre de la
 * clase Java del productor (los consumidores tienen su propia copia del evento).
 * La configuracion replica la de config-repo/ms-transacciones.yml.
 */
class TransaccionCreadaEventContractTest {

    private final TransaccionCreadaEvent evento =
            new TransaccionCreadaEvent(4L, 101L, 500.0, "credito", LocalDate.of(2026, 9, 24));

    private JsonSerializer<TransaccionCreadaEvent> serializador() {
        JsonSerializer<TransaccionCreadaEvent> s = new JsonSerializer<>();
        s.configure(Map.of("spring.json.add.type.headers", false), false);
        return s;
    }

    @Test
    void laFechaSePublicaComoTextoIso() {
        byte[] json = serializador().serialize("transacciones", new RecordHeaders(), evento);
        assertEquals(
                "{\"transaccionId\":4,\"cuentaId\":101,\"monto\":500.0,\"tipo\":\"credito\",\"fecha\":\"2026-09-24\"}",
                new String(json, StandardCharsets.UTF_8));
    }

    @Test
    void noSeEnvianHeadersConNombresDeClasesJava() {
        RecordHeaders headers = new RecordHeaders();
        serializador().serialize("transacciones", headers, evento);
        assertNull(headers.lastHeader("__TypeId__"));
    }
}
