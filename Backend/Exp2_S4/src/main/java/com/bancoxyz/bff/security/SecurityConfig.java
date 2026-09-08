package com.bancoxyz.bff.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad especifica por canal (requisito de la actividad).
 *
 * Cada BFF tiene su propio canal de acceso con un rol dedicado, de modo que
 * las credenciales de un canal no sirven en otro:
 *   - /api/web/**    -> rol WEB
 *   - /api/movil/**  -> rol MOVIL
 *   - /api/cajero/** -> rol CAJERO
 *
 * Ademas, dentro del canal de cajero, la operacion critica de retiro (POST)
 * queda restringida al rol CAJERO explicitamente. Se usa autenticacion basica
 * HTTP y usuarios en memoria por simplicidad academica; en produccion se
 * reemplazaria por un proveedor de identidad y tokens por canal.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/web/**").hasRole("WEB")
                .requestMatchers("/api/movil/**").hasRole("MOVIL")
                .requestMatchers("/api/cajero/**").hasRole("CAJERO")
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated())
            .httpBasic(basic -> {})
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    /**
     * Un usuario por canal. Cada credencial solo abre su propio BFF.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails web = User.withUsername("web_user")
                .password(encoder.encode("web123")).roles("WEB").build();
        UserDetails movil = User.withUsername("movil_user")
                .password(encoder.encode("movil123")).roles("MOVIL").build();
        UserDetails cajero = User.withUsername("cajero_user")
                .password(encoder.encode("cajero123")).roles("CAJERO").build();
        return new InMemoryUserDetailsManager(web, movil, cajero);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
