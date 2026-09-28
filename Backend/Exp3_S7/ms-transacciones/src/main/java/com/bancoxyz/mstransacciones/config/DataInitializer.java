package com.bancoxyz.mstransacciones.config;

import com.bancoxyz.mstransacciones.model.Transaccion;
import com.bancoxyz.mstransacciones.repository.TransaccionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(TransaccionRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(new Transaccion(101L, LocalDate.of(2024,1,1), 1000.0, "debito"));
            repo.save(new Transaccion(101L, LocalDate.of(2024,1,5), 1500.0, "credito"));
            repo.save(new Transaccion(102L, LocalDate.of(2024,1,3), 800.0, "debito"));
        };
    }
}
