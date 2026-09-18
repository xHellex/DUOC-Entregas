package com.bancoxyz.mstransacciones.service;

import com.bancoxyz.mstransacciones.model.Transaccion;
import com.bancoxyz.mstransacciones.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransaccionService {
    private final TransaccionRepository repo;
    public TransaccionService(TransaccionRepository repo) { this.repo = repo; }
    public List<Transaccion> listar() { return repo.findAll(); }
}
