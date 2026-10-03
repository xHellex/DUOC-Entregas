package com.bancoxyz.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Verifica que el contexto del Authorization Server se cargue correctamente
 * (cliente registrado, JWK y cadenas de seguridad). Se deshabilita el registro
 * en Eureka para que la prueba no dependa de servicios externos.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class AuthServerApplicationTests {

    @Test
    void contextLoads() {
    }
}
