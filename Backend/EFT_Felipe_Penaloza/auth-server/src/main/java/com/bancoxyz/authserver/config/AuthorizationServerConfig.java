package com.bancoxyz.authserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

/**
 * Configuracion del Authorization Server (OAuth 2.0).
 *
 * Define:
 *  - La cadena de seguridad del propio servidor de autorizacion (endpoints
 *    estandar: /oauth2/token, /oauth2/jwks, etc.).
 *  - El cliente registrado del Banco XYZ, que usa el flujo "client_credentials"
 *    (comunicacion maquina-a-maquina entre servicios/clientes de confianza).
 *  - La clave RSA con la que se firman los JWT y se expone el JWK Set.
 *  - El emisor (issuer) del servidor, configurable por entorno.
 */
@Configuration
public class AuthorizationServerConfig {

    /**
     * Cadena de filtros del servidor de autorizacion. Aplica la configuracion
     * por defecto de Spring Authorization Server, que habilita los endpoints
     * del protocolo OAuth2 (token, jwks, introspection, revocation).
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.build();
    }

    /**
     * Cadena de seguridad por defecto para el resto de peticiones del servidor
     * (por ejemplo los endpoints de actuator para healthcheck).
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /**
     * Cliente de confianza del Banco XYZ. Utiliza el flujo client_credentials:
     * un cliente (p. ej. el BFF, un cajero o un servicio interno) presenta su
     * client_id y client_secret y obtiene un access token para consumir los
     * microservicios protegidos. El secreto se almacena cifrado con BCrypt.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

        RegisteredClient clienteBanco = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("bancoxyz-client")
            // Se guarda cifrado; el DelegatingPasswordEncoder lo reconoce por el prefijo {bcrypt}
            .clientSecret("{bcrypt}" + bcrypt.encode("bancoxyz-secret"))
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            // Scopes de negocio (podrian usarse para autorizacion fina por endpoint)
            .scope("cuentas.read")
            .scope("cuentas.write")
            .scope("pagos.read")
            .scope("pagos.write")
            .scope("clientes.read")
            .scope("clientes.write")
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .build())
            .build();

        return new InMemoryRegisteredClientRepository(clienteBanco);
    }

    /**
     * Codificador de contrasenas delegado: reconoce el formato {bcrypt}...,
     * usado para validar el client_secret durante la emision de tokens.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Fuente de claves (JWK). Genera un par de claves RSA al arrancar; con la
     * privada se firman los JWT y la publica se expone en /oauth2/jwks para que
     * los Resource Servers validen la firma de los tokens.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generarClaveRsa();
        RSAPublicKey publica = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privada = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publica)
            .privateKey(privada)
            .keyID(UUID.randomUUID().toString())
            .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * Decodificador JWT del propio servidor (usado internamente por los
     * endpoints OAuth2).
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Ajustes del servidor de autorizacion. El issuer identifica al emisor de
     * los tokens; se parametriza por entorno (local vs. docker) con la
     * propiedad app.issuer-uri.
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @Value("${app.issuer-uri:http://localhost:9000}") String issuerUri) {
        return AuthorizationServerSettings.builder()
            .issuer(issuerUri)
            .build();
    }

    private static KeyPair generarClaveRsa() {
        try {
            KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
            generador.initialize(2048);
            return generador.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el par de claves RSA", e);
        }
    }
}
