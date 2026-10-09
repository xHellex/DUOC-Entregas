package com.bancoxyz.msclientes.service;

import com.bancoxyz.msclientes.model.Cliente;
import com.bancoxyz.msclientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository repo;
    public ClienteService(ClienteRepository repo) { this.repo = repo; }

    public List<Cliente> listar() { return repo.findAll(); }
    public Optional<Cliente> obtener(Long id) { return repo.findById(id); }
}
