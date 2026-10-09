package com.bancoxyz.mspagos.service;

import com.bancoxyz.mspagos.model.Pago;
import com.bancoxyz.mspagos.repository.PagoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PagoService {
    private final PagoRepository repo;
    public PagoService(PagoRepository repo) { this.repo = repo; }

    public List<Pago> listar() { return repo.findAll(); }

    public Pago crear(Pago t) { return repo.save(t); }
}
