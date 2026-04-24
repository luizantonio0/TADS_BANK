package com.bantads.cliente.service;

import com.bantads.cliente.dto.ClienteResponseDTO;
import com.bantads.cliente.dto.gerente.DefinirGerenteOutputDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ClienteService {

    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder encoder;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    private final Map<UUID, CompletableFuture<AprovarClienteResponseDTO>> aprovarClienteResponses = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<ClienteResponseDTO>> criarClienteResponses = new ConcurrentHashMap<>();

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
    public void finishCriarCliente(OrchestrationRequestResultDTO result) throws Exception {

        var ok = true;
        var errors = new ArrayList<String>();

        try {
            if (result == null) {
                throw new Exception("Resposta nula do orquestrador");
            }

            if(result.failed()) {
                errors.addAll(result.errors().values());
                ok = false;
            }

            if (!result.payloads().containsKey(OrchestrationKeys.MS_GERENTE)
                    || !result.payloads().containsKey(OrchestrationKeys.MS_CLIENTE)) {
                throw new Exception("Payloads esperados não encontrados");
            }

            ObjectMapper mapper = new ObjectMapper();

            var gerenteOutput = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_GERENTE), DefinirGerenteOutputDTO.class);
            var clienteDTO = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_CLIENTE), ClienteRequestDTO.class);

            Cliente cliente = new Cliente(clienteDTO);
            cliente.setGerente(gerenteOutput.idGerente());
            clienteRepository.save(cliente);

            ClienteResponseDTO dto = new ClienteResponseDTO();
            if(criarClienteResponses.containsKey(result.idOrchestration())) {
                criarClienteResponses.get(result.idOrchestration()).complete(dto);
            }

        } catch (Exception ex) {
            errors.add(ex.getMessage());
            ok = false;
            throw new Exception(String.join(",", errors));
        } finally {
            if(result != null) {
                rabbitTemplate.convertAndSend(
                        "orchestration.confirm",
                        "orchestration.confirm",
                        new OrchestrationConfirmDTO(result.idOrchestration(), String.join(",", errors), ok)
                );
            }
        }
    }

    @Transactional
    public CompletableFuture<ClienteResponseDTO> startCriarCliente(ClienteRequestDTO dto) throws Exception {

        var idOrchestration = UUID.randomUUID();

        try {

            ObjectMapper mapper = new ObjectMapper();

            var request = new OrchestrationRequestDTO(
                    idOrchestration,
                    false,
                    List.of(
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_CLIENTE, OrchestrationKeys.CREATE_CLIENTE_COMMAND, mapper.writeValueAsString(dto))
                    )
            );

            var completable = new CompletableFuture<ClienteResponseDTO>();
            criarClienteResponses.put(idOrchestration, completable);
            rabbitTemplate.convertSendAndReceive(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

            return completable;

        } catch (Exception ex) {
            throw ex;
        }

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
        