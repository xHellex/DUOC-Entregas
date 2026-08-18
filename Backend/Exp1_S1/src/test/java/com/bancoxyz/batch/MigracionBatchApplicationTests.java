package com.bancoxyz.batch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de humo: verifica que el contexto de Spring arranca y que los
 * Jobs y Steps quedan correctamente configurados.
 *
 * Se fuerza el perfil "h2" para que el test no dependa de una instancia
 * MySQL externa. Con H2 en memoria, BatchRunner ejecuta los tres jobs
 * durante el arranque del contexto usando los datos de semana_1.
 */
@SpringBootTest
@ActiveProfiles("h2")
class MigracionBatchApplicationTests {

    @Test
    void contextLoads() {
    }
}
