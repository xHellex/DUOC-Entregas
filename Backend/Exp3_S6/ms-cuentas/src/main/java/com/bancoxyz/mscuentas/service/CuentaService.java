package com.bancoxyz.mscuentas.service;

import com.bancoxyz.mscuentas.model.Cuenta;
import com.bancoxyz.mscuentas.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CuentaService {
    private final CuentaRepository repo;
    public CuentaService(CuentaRepository repo) { this.repo = repo; }
    public List<Cuenta> listar() { return repo.findAll(); }
    public Optional<Cuenta> obtener(Long id) { return repo.findById(id); }
}
