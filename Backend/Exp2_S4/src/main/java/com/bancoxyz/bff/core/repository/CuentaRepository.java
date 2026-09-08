package com.bancoxyz.bff.core.repository;

import com.bancoxyz.bff.core.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> { }
