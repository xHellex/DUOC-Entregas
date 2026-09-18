package com.bancoxyz.mstarjetas.service;

import com.bancoxyz.mstarjetas.model.Tarjeta;
import com.bancoxyz.mstarjetas.repository.TarjetaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TarjetaService {
    private final TarjetaRepository repo;
    public TarjetaService(TarjetaRepository repo) { this.repo = repo; }
    public List<Tarjeta> listar() { return repo.findAll(); }
}
