package com.bantads.cliente.service;

import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.exception.BadRequestException;
import com.bantads.cliente.exception.NotFoundException;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ClienteMapper mapper;

    public List<Cliente> findAll() {
        return clienteRepository.findAll(); 
    }
    
    public Cliente findByCpf(String cpf) throws NotFoundException{
        return clienteRepository.findByCpf(cpf).orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    public Cliente cadastrarCliente(ClienteRequestDTO dto) throws BadRequestException {
        var cpf = dto.cpf().replaceAll("[^0-9]", "");
        if(clienteRepository.existsByCpf(cpf)) {
            throw new BadRequestException("Este CPF já está em uso!");
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
            throw new NotFoundException("Cliente não encontrado");
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
            var whitelist = List.of("12912861012", "09506382000", "85733854057", "58872160006", "76179646090");
            var cpf = clienteRepository.findById(uuid);
            if(cpf.isPresent() && !whitelist.contains(cpf.get().getCpf()))
                clienteRepository.deleteById(uuid);
        }
    }

}
        