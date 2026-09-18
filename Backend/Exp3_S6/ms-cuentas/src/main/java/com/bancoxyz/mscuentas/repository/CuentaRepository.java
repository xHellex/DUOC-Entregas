package com.bancoxyz.mscuentas.repository;

import com.bancoxyz.mscuentas.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> { }
