package com.bancoxyz.bff.core.repository;

import com.bancoxyz.bff.core.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {
    List<Tarjeta> findByCuentaId(Long cuentaId);
}
