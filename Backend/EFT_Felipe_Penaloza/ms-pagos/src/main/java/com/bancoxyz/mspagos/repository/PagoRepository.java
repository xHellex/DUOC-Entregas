package com.bancoxyz.mspagos.repository;

import com.bancoxyz.mspagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> { }
