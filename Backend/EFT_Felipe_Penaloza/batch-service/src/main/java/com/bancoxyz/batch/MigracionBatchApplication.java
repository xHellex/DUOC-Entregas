package com.bancoxyz.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion de migracion batch del Banco XYZ.
 * Al arrancar, Spring Boot detecta los Jobs configurados y los ejecuta
 * automaticamente (segun spring.batch.job.enabled en application.properties).
 */
@SpringBootApplication
public class MigracionBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(MigracionBatchApplication.class, args);
    }
}
