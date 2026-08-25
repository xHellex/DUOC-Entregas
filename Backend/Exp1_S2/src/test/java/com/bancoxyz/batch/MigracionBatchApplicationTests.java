package com.bancoxyz.batch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de humo: verifica que el contexto de Spring arranca con todos los
 * componentes avanzados (TaskExecutor, SkipPolicy, Decider, listeners) y que
 * los tres Jobs quedan correctamente configurados. Se fuerza el perfil h2
 * para no depender de una instancia MySQL externa.
 */
@SpringBootTest
@ActiveProfiles("h2")
class MigracionBatchApplicationTests {

    @Test
    void contextLoads() {
    }
}
