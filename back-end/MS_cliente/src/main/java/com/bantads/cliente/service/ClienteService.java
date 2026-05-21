package com.bantads.cliente.service;

import org.springframework.data.history.RevisionMetadata;
import com.bantads.cliente.dto.ClienteResumidoDTO;
import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.exception.BadRequestException;
import com.bantads.cliente.exception.ForbiddenException;
import com.bantads.cliente.exception.HttpException;
import com.bantads.cliente.exception.NotFoundException;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.repository.ClienteRepository;

import org.hibernate.envers.RevisionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ClienteMapper mapper;

    public List<ClienteResumidoDTO> findClientes(String cpfLogado, String profileLogado, String filtro) throws HttpException {
        System.out.println(cpfLogado + " " + profileLogado + " " + filtro);

        var isGerente = profileLogado.equalsIgnoreCase("GERENTE");
        var isAdmin = profileLogado.equalsIgnoreCase("ADMINISTRADOR");

        if(!isGerente && !isAdmin) {
            throw new ForbiddenException("Você não tem permissão para efetuar esta operação");
        }

        if(filtro.equals("para_aprovar")) {
            if(!isGerente) throw new ForbiddenException("Você não tem permissão para efetuar esta operação");
            return clienteRepository.findByCpfGerenteAndAprovado(cpfLogado, false).stream().map(ClienteResumidoDTO::from).toList();
        }

        if(filtro.equalsIgnoreCase("melhores_clientes")) {
            
        }

        if(isAdmin) {
            return clienteRepository.findAll().stream().map(ClienteResumidoDTO::from).toList();
        }

        return clienteRepository.findByCpfGerenteAndAprovado(cpfLogado, true).stream().map(ClienteResumidoDTO::from).toList();
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

        var whitelist = List.of("12912861012", "09506382000", "85733854057", "58872160006", "76179646090");

        if (content.size() >= 2) {
            var rev = content.get(1);
            var tipo = rev.getMetadata().getRevisionType();

            if (tipo != RevisionMetadata.RevisionType.DELETE) {
                clienteRepository.save(rev.getEntity());
                return;
            }

            // se n foi nem insert nem update, a ultima versão é um delete, ou seja, o obj ainda nao existia
            clienteRepository.deleteById(rev.getEntity().getId());
        } else {
            var cpf = clienteRepository.findById(uuid);
            if(cpf.isPresent() && !whitelist.contains(cpf.get().getCpf()))
                clienteRepository.deleteById(uuid);
        }
    }

}
        