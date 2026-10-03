package com.bancoxyz.mstarjetas.config;

import com.bancoxyz.mstarjetas.model.Tarjeta;
import com.bancoxyz.mstarjetas.repository.TarjetaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(TarjetaRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(new Tarjeta(101L, "credito", "Visa", 2000.0, true));
            repo.save(new Tarjeta(101L, "debito", "Mastercard", 5000.0, true));
            repo.save(new Tarjeta(102L, "credito", "Visa", 3500.0, false));
        };
    }
}
