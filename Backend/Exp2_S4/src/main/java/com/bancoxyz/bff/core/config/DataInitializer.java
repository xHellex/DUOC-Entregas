package com.bancoxyz.bff.core.config;

import com.bancoxyz.bff.core.model.Cuenta;
import com.bancoxyz.bff.core.model.Transaccion;
import com.bancoxyz.bff.core.repository.CuentaRepository;
import com.bancoxyz.bff.core.repository.TransaccionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/**
 * Carga datos de ejemplo del Banco XYZ al arrancar, derivados del dominio
 * trabajado en el procesamiento batch (cuentas con saldo y tipo, y sus
 * transacciones). Permite probar los tres BFF sin configuracion adicional.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(CuentaRepository cuentaRepo,
                                      TransaccionRepository transaccionRepo) {
        return args -> {
            if (cuentaRepo.count() > 0) return;

            cuentaRepo.save(new Cuenta(101L, "John Doe", "ahorro", 5000.0, "1234567890123456"));
            cuentaRepo.save(new Cuenta(102L, "Jane Smith", "prestamo", 8000.0, "2345678901234567"));
            cuentaRepo.save(new Cuenta(103L, "Bob Johnson", "hipoteca", 12000.0, "3456789012345678"));
            cuentaRepo.save(new Cuenta(105L, "Charlie Green", "ahorro", 7000.0, "4567890123456789"));
            cuentaRepo.save(new Cuenta(107L, "Diana Prince", "prestamo", 15000.0, "5678901234567890"));

            transaccionRepo.save(new Transaccion(101L, LocalDate.of(2024,1,1), 1000.0, "debito", "Compra supermercado"));
            transaccionRepo.save(new Transaccion(101L, LocalDate.of(2024,1,5), 1500.0, "credito", "Transferencia recibida"));
            transaccionRepo.save(new Transaccion(102L, LocalDate.of(2024,1,3), 800.0, "debito", "Pago servicios"));
            transaccionRepo.save(new Transaccion(103L, LocalDate.of(2024,1,7), 2000.0, "credito", "Deposito"));
            transaccionRepo.save(new Transaccion(105L, LocalDate.of(2024,1,10), 500.0, "debito", "Retiro cajero"));
        };
    }
}
