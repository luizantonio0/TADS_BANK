package com.bantads.cliente.service;

import com.bantads.cliente.dto.ClienteDTO;
import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.enums.LogStatus;
import com.bantads.cliente.exception.BadRequestException;
import com.bantads.cliente.exception.ForbiddenException;
import com.bantads.cliente.exception.HttpException;
import com.bantads.cliente.exception.NotFoundException;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.model.LogStatusCliente;
import com.bantads.cliente.repository.ClienteRepository;
import com.bantads.cliente.repository.LogStatusRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private LogStatusRepository logStatusRepository;
    @Autowired private ClienteMapper mapper;

    public List<Cliente> findClientes(String cpfLogado, String profileLogado, String filtro, String nome) throws HttpException {
        System.out.println(cpfLogado + " " + profileLogado + " " + filtro);

        var isGerente = profileLogado.equalsIgnoreCase("GERENTE");
        var isAdmin = profileLogado.equalsIgnoreCase("ADMINISTRADOR");

        if(!isGerente && !isAdmin) {
            throw new ForbiddenException("Você não tem permissão para efetuar esta operação");
        }

        if(filtro.equals("para_aprovar")) {
            if(!isGerente) throw new ForbiddenException("Você não tem permissão para efetuar esta operação");
            return clienteRepository.findByCpfGerenteAndAprovado(cpfLogado, false);
        }

        if(isAdmin) {
            return clienteRepository.findAll();
        }

        return null;
    }

    public Map<String, String> findNomesByCpf(List<String> cpfs) {
        return clienteRepository.findByCpfIn(cpfs).stream().collect(Collectors.toMap(c->c.getCpf(), c->c.getNome()));
    } 
    
    public Cliente findByCpf(String cpf) throws NotFoundException{
        return clienteRepository.findByCpf(cpf).orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    public List<Cliente> findByGerente(String cpf) {
        return clienteRepository.findByGerente(cpf, "");
    }

    public Map<String, List<ClienteDTO>> findClientesByGerentes(String cpfs) {
        var cpfsList = cpfs.split(",");
        var maps = new HashMap<String, List<ClienteDTO>>();
        for(var cpf : cpfsList)
            maps.put(cpf, clienteRepository.findByGerente(cpf, "").stream().map(ClienteDTO::from).toList());
        return maps;
    }

    public List<Cliente> findClientesByGerente(String cpf) {
        return clienteRepository.findByGerente(cpf, "");
    }

    public List<Cliente> findByCpf(List<String> cpf) {
        return clienteRepository.findByCpfIn(cpf);
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

    @Transactional
    public Cliente aprovarCliente(String cpf) throws Exception {
        var cliente = clienteRepository.findByCpf(cpf);
        if(cliente.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado");
        }
        cliente.get().setAprovado(true);
        logStatusRepository.save(new LogStatusCliente(cliente.get().getId(), cpf, LogStatus.APROVADO, "", LocalDateTime.now()));
        clienteRepository.save(cliente.get());
        return cliente.get();
    }

    @Transactional
    public Cliente rejeitarCliente(String cpf, String motivo) throws Exception {
        var cliente = clienteRepository.findByCpf(cpf);
        if(cliente.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado");
        }
        logStatusRepository.save(new LogStatusCliente(cliente.get().getId(), cpf, LogStatus.REJEITADO, motivo, LocalDateTime.now()));
        clienteRepository.delete(cliente.get());
        return cliente.get();
    }

    public void rollbackLogStatus(UUID uuid) throws Exception {
        Page<Revision<Integer, LogStatusCliente>> revisions = logStatusRepository.findRevisions(uuid, PageRequest.of(0, 2));
        List<Revision<Integer, LogStatusCliente>> content = revisions.getContent();

        if (content.size() >= 2) {
            var rev = content.get(1);
            logStatusRepository.save(rev.getEntity());
        } else {
            var cpf = clienteRepository.findById(uuid);
            if(cpf.isPresent())
                logStatusRepository.deleteById(uuid);
        }
    }

    public void rollbackCliente(UUID uuid) throws Exception {

        Page<Revision<Integer, Cliente>> revisions = clienteRepository.findRevisions(uuid, PageRequest.of(0, 2));
        List<Revision<Integer, Cliente>> content = revisions.getContent();

        var whitelist = List.of("12912861012", "09506382000", "85733854057", "58872160006", "76179646090");

        if (content.size() >= 2) {
            var rev = content.get(1);
            clienteRepository.save(rev.getEntity());
        } else {
            var cpf = clienteRepository.findById(uuid);
            if(cpf.isPresent() && !whitelist.contains(cpf.get().getCpf()))
                clienteRepository.deleteById(uuid);
        }
    }

}
        