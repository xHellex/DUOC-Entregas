package com.bancoxyz.bancoservicios.repository;

import com.bancoxyz.bancoservicios.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> { }
