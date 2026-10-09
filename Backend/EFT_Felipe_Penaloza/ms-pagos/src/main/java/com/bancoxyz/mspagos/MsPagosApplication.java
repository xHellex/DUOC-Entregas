package com.bancoxyz.mspagos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Microservicio de Pagos. Se registra en Eureka, toma su configuracion
 * del Config Server, protege sus endpoints con JWT y aplica tolerancia a
 * fallos con Resilience4j.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MsPagosApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsPagosApplication.class, args);
    }
}
