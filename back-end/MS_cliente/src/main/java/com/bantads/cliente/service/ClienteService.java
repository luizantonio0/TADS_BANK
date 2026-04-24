package com.bantads.cliente.service;

import com.bantads.shared.dto.*;
import com.bantads.cliente.dto.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.AprovarClienteResponseDTO;
import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.dto.auth.CredentialsCreateDTO;
import com.bantads.cliente.dto.conta.ContaCreateInputDTO;
import com.bantads.cliente.dto.conta.ContaCreateOutputDTO;
import com.bantads.cliente.exceptions.AccountAlredyExists;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.orchestration.OrchestrationKeys;
import com.bantads.cliente.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ClienteService {

    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder encoder;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    public ClienteService(RabbitTemplate rabbitTemplate, ClienteRepository clienteRepository, ClienteMapper mapper, PasswordEncoder encoder) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
        this.encoder = encoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll(); 
    }
    
    public Cliente findByCpf(String cpf){
        return clienteRepository.findByCpf(cpf).orElse(null);
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

    @Transactional
    public AprovarClienteResponseDTO aprovarCliente(String cpf) throws Exception {

        var cliente = clienteRepository.findByCpf(cpf);
        if(cliente.isEmpty()) throw new IllegalStateException("Cliente não encontrado");
        var c = cliente.get();
        c.setAprovado(true);
        clienteRepository.save(c);

        var idOrchestration = UUID.randomUUID();

        var credentialsDTO = new CredentialsCreateDTO(c.getEmail(), cpf, ThreadLocalRandom.current().nextInt(1000, 10000) + "");
        var contaDTO = new ContaCreateInputDTO(cpf, c.getSalario());

        ObjectMapper mapper = new ObjectMapper();

        var request = new OrchestrationRequestDTO(
                idOrchestration,
                List.of(
                        new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_AUTH, OrchestrationKeys.CREATE_CREDENTIALS_COMMAND, mapper.writeValueAsString(credentialsDTO)),
                        new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_CONTA, OrchestrationKeys.CREATE_CONTA_COMMAND, mapper.writeValueAsString(contaDTO))
                )
        );

        var result = (OrchestrationRequestResultDTO) rabbitTemplate.convertSendAndReceive(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

        if(result == null || result.failed()) {
            throw new Exception(result == null ?
                    "Resultado de orquestração nulo" : "Orquestração falhou: " + String.join(",", result.errors().values()));
        }

        if (!result.payloads().containsKey(OrchestrationKeys.MS_CONTA)) {
            throw new Exception("Serviço de conta não retornou payload");
        }

        ContaCreateOutputDTO contaOutput = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_CONTA), ContaCreateOutputDTO.class);

        return new AprovarClienteResponseDTO(
                c.getCpf(),
                contaOutput.numero(),
                contaOutput.saldo(),
                contaOutput.limite(),
                "Gerente 1",
                "Criacao top"
        );
    }

    public void rollbackCliente(UUID uuid) throws Exception {
        Page<Revision<Integer, Cliente>> revisions = clienteRepository.findRevisions(uuid, PageRequest.of(0, 2, Sort.by("revisionNumber").descending()));
        List<Revision<Integer, Cliente>> content = revisions.getContent();
        if (content.size() >= 2) {
            var revision = content.get(1).getEntity();
            clienteRepository.save(revision);
        } else {
            clienteRepository.deleteById(uuid);
        }
    }

}
        