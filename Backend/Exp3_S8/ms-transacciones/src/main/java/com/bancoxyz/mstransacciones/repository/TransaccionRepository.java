package com.bancoxyz.mstransacciones.repository;

import com.bancoxyz.mstransacciones.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> { }
