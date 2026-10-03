package com.bancoxyz.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Servidor de configuracion centralizada. Sirve los archivos de configuracion
 * de todos los microservicios desde un repositorio comun (carpeta config-repo),
 * de modo que cada microservicio obtiene su configuracion de aqui al arrancar.
 * Puerto: 8888.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
