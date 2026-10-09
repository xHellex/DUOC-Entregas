package com.bancoxyz.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicacion BFF del Banco XYZ. Levanta un unico servicio Spring Boot que
 * expone tres Backends for Frontend: /api/web, /api/movil y /api/cajero,
 * cada uno adaptado a su cliente.
 */
@SpringBootApplication
public class BffBancoxyzApplication {
    public static void main(String[] args) {
        SpringApplication.run(BffBancoxyzApplication.class, args);
    }
}
