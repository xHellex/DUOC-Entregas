package com.bancoxyz.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Authorization Server OAuth2.0 del Banco XYZ.
 *
 * Centraliza la seguridad del ecosistema de microservicios: emite access
 * tokens (JWT) firmados con RSA y publica el JWK Set que usan los Resource
 * Servers (ms-cuentas, ms-pagos, ms-clientes) para validarlos. De
 * esta forma los microservicios ya no gestionan credenciales: solo validan
 * tokens, aplicando el patron de "delegacion de seguridad".
 */
@SpringBootApplication
public class AuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
