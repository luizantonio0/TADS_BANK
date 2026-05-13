package com.bantads.cliente.service;

import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.exceptions.AccountAlredyExists;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ClienteService {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ClienteMapper mapper;

    public List<Cliente> findAll() {
        return clienteRepository.findAll(); 
    }
    
    public Cliente findByCpf(String cpf){
        return clienteRepository.findByCpf(cpf).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado!"));
    }

    public Cliente cadastrarCliente(ClienteRequestDTO dto) throws AccountAlredyExists {
        if(clienteRepository.existsByCpf(dto.cpf())) {
            throw new AccountAlredyExists("Este CPF já está em uso!");
        }
        Cliente cliente = new Cliente(dto);
        return clienteRepository.save(cliente);
    }
    
    public Cliente update(AlterarDadosClienteDTO dto, String cpf){
        var clienteAtual = clienteRepository.findByCpf(cpf);

        if(clienteAtual.isEmpty()) return null;

        var cliente = clienteAtual.get();

        mapper.updateEntityFromDto(dto, cliente);

        return clienteRepository.save(cliente);
    }

    public Cliente aprovarCliente(String cpf) throws Exception {
        var cliente = clienteRepository.findByCpf(cpf);
        if(cliente.isEmpty()) {
            throw new Exception("Cliente não encontrado");
        }
        cliente.get().setAprovado(true);
        clienteRepository.save(cliente.get());
        return cliente.get();
    }

    public void rollbackCliente(UUID uuid) throws Exception {
        Page<Revision<Integer, Cliente>> revisions = clienteRepository.findRevisions(uuid, PageRequest.of(0, 2));
        List<Revision<Integer, Cliente>> content = revisions.getContent();
        if (content.size() >= 2) {
            var revision = content.get(1).getEntity();
            clienteRepository.save(revision);
        } else {
            clienteRepository.deleteById(uuid);
        }
    }

}
        