package com.bancoxyz.mstarjetas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Seguridad del microservicio: stateless con JWT. El endpoint de login y el
 * de health quedan publicos; el resto exige un token valido.
 */
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/actuator/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthFilter(jwtUtil),
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
