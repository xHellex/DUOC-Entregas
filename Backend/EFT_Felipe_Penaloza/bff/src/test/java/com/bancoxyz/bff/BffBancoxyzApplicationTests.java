package com.bancoxyz.bff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de humo: verifica que el contexto arranca con los tres BFF
 * (web, movil, cajero), el servicio central y la seguridad por canal.
 */
@SpringBootTest
class BffBancoxyzApplicationTests {

    @Test
    void contextLoads() {
    }
}
