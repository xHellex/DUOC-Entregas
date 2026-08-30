package com.bancoxyz.batch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de humo: verifica que el contexto de Spring arranca con todos los
 * componentes de particionamiento (RangoPartitioner, TaskExecutorPartitionHandler,
 * minionSteps @StepScope) y que los tres Jobs quedan correctamente configurados.
 */
@SpringBootTest
@ActiveProfiles("h2")
class MigracionBatchApplicationTests {

    @Test
    void contextLoads() {
    }
}
