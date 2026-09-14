package com.bancoxyz.bancoservicios.repository;

import com.bancoxyz.bancoservicios.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {
    List<Tarjeta> findByCuentaId(Long cuentaId);
}
