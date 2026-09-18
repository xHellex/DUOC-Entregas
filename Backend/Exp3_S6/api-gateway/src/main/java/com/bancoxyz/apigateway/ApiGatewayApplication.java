package com.bancoxyz.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway. Punto de entrada unico del sistema: enruta las peticiones
 * externas a los microservicios correspondientes, descubriendolos por su
 * nombre en Eureka (sin URLs fijas). Puerto: 8080.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
