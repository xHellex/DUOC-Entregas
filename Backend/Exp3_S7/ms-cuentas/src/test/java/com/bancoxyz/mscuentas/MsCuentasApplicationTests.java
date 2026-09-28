package com.bancoxyz.mscuentas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
class MsCuentasApplicationTests {

    @Test
    void contextLoads() {
    }
}
