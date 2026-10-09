package com.bancoxyz.msclientes.config;

import com.bancoxyz.msclientes.model.Cliente;
import com.bancoxyz.msclientes.repository.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(ClienteRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(new Cliente(1L, "11.111.111-1", "Ana Perez",  "ana.perez@bancoxyz.cl",  "preferente", 101L));
            repo.save(new Cliente(2L, "22.222.222-2", "Luis Soto",  "luis.soto@bancoxyz.cl",  "estandar",   102L));
            repo.save(new Cliente(3L, "33.333.333-3", "Marta Rojas","marta.rojas@bancoxyz.cl","empresa",    103L));
        };
    }
}
