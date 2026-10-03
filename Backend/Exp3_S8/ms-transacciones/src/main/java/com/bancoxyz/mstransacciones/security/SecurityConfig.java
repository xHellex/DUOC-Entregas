package com.bancoxyz.mstransacciones.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad del microservicio configurado como OAuth2 Resource Server.
 *
 * Ya no se gestiona la autenticacion localmente (se elimino el JWT propio de
 * la S7). Ahora el microservicio DELEGA la seguridad en el Authorization
 * Server: cada peticion debe traer un access token (JWT) en la cabecera
 * "Authorization: Bearer ...". El token se valida de forma stateless contra
 * el JWK Set publicado por el Authorization Server
 * (propiedad spring.security.oauth2.resourceserver.jwt.jwk-set-uri).
 *
 * Endpoints de infraestructura (actuator, consola H2) quedan publicos para
 * facilitar la operacion y la evidencia; el resto exige un token valido.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated())
            // Habilita la validacion de tokens OAuth2 (JWT) emitidos por el Auth Server.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            // Necesario para que la consola H2 se muestre en un frame.
            .headers(h -> h.frameOptions(f -> f.disable()));
        return http.build();
    }
}
