package com.bantads.cliente.service;

import com.bantads.cliente.dto.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.AprovarClienteDTO;
import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.exceptions.AccountAlredyExists;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll(); 
    }
    
    public Cliente findByCpf(String cpf){
        return clienteRepository.findByCpf(cpf).orElse(null);
    }

    public Cliente save(ClienteRequestDTO dto) throws AccountAlredyExists {
        boolean existe = clienteRepository.existsByCpf(dto.cpf());

        if(existe) throw new AccountAlredyExists("Não é possivel cadastrar um cliente com o mesmo CPF");

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

    public AprovarClienteDTO aprovar(String cpf, Object gerente, Object conta){
        var clienteAtual = clienteRepository.findByCpf(cpf);

        if(clienteAtual.isEmpty()) return null;

        var cliente = clienteAtual.get();

        //TODO: Trocar por senha aleatoria
        cliente.setSenha("123456");

        clienteRepository.save(cliente);

        return new AprovarClienteDTO(cliente.getCpf(), "", BigDecimal.ZERO, BigDecimal.ZERO, "","","");
    }
}
        