package com.bancoxyz.msclientes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de carga de contexto. Se deshabilitan Eureka y Config Server y se
 * define un JWK Set URI de prueba (el NimbusJwtDecoder lo consulta de forma
 * perezosa, por lo que el contexto carga sin necesidad del Auth Server).
 */
@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false",
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9000/oauth2/jwks"
})
class MsClientesApplicationTests {

    @Test
    void contextLoads() {
    }
}
