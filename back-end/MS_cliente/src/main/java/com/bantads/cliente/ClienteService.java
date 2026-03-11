package com.bantads.cliente;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> findAll() { 
        return clienteRepository.findAll(); 
    }
    
    public Cliente findById(UUID id){
        return clienteRepository.findById(id).orElse(null);
    }

    public Cliente save(Cliente cliente){
        return clienteRepository.save(cliente);
    }
    
    public Cliente update(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public void delete(UUID id){
        clienteRepository.deleteById(id);
    }
}
        