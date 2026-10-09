package com.bancoxyz.mscuentas.config;

import com.bancoxyz.mscuentas.model.Cuenta;
import com.bancoxyz.mscuentas.repository.CuentaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(CuentaRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(new Cuenta(101L, "John Doe", "ahorro", 5000.0));
            repo.save(new Cuenta(102L, "Jane Smith", "prestamo", 8000.0));
            repo.save(new Cuenta(103L, "Bob Johnson", "hipoteca", 12000.0));
        };
    }
}
