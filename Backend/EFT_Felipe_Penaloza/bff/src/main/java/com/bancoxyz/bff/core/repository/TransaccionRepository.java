package com.bancoxyz.bff.core.repository;

import com.bancoxyz.bff.core.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}
