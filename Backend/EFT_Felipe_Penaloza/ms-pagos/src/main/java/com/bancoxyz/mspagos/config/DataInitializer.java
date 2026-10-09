package com.bancoxyz.mspagos.config;

import com.bancoxyz.mspagos.model.Pago;
import com.bancoxyz.mspagos.repository.PagoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(PagoRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(new Pago(101L, LocalDate.of(2024,1,1), 1000.0, "debito"));
            repo.save(new Pago(101L, LocalDate.of(2024,1,5), 1500.0, "credito"));
            repo.save(new Pago(102L, LocalDate.of(2024,1,3), 800.0, "debito"));
        };
    }
}
