package com.bantads.cliente.service;

import com.bantads.cliente.dto.AprovarClienteResponseDTO;
import com.bantads.cliente.dto.ClienteCreateResponseDTO;
import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.dto.saga.output.CreateClienteOutputDTO;
import com.bantads.cliente.dto.saga.output.DefinirGerenteOutputDTO;
import com.bantads.cliente.orchestration.OrchestrationKeys;
import com.bantads.cliente.repository.ClienteRepository;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.bantads.shared.dto.OrchestrationConfirmDTO;
import com.bantads.shared.dto.OrchestrationRequestDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrchestrationService {

    @Autowired private ClienteRepository repository;

    @Autowired private RabbitTemplate rabbitTemplate;
    private final Map<UUID, CompletableFuture<AprovarClienteResponseDTO>> aprovarClienteResponses = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<ClienteCreateResponseDTO>> criarClienteResponses = new ConcurrentHashMap<>();

    public boolean isCriarClienteSaga(UUID idOrchestration) {
        return criarClienteResponses.containsKey(idOrchestration);
    }

    public boolean isAprovarClienteSaga(UUID idOrchestration) {
        return aprovarClienteResponses.containsKey(idOrchestration);
    }

    @Transactional
    public void finishCriarCliente(OrchestrationRequestResultDTO result) {

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
            var clienteDTO = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_CLIENTE), CreateClienteOutputDTO.class);

            var clienteOptional = repository.findByCpf(clienteDTO.cpf());
            if(clienteOptional.isEmpty()) {
                throw new Exception("Cliente não encontrado");
            }

            var cliente = clienteOptional.get();
            cliente.setGerente(gerenteOutput.idGerente());
            repository.save(cliente);

            ClienteCreateResponseDTO dto = new ClienteCreateResponseDTO(
                    cliente.getCpf(),
                    cliente.getEmail(),
                    cliente.getNome(),
                    cliente.getTelefone(),
                    cliente.getSalario(),
                    cliente.getEndereco(),
                    cliente.getCep(),
                    cliente.getCidade(),
                    ""
            );

            if(criarClienteResponses.containsKey(result.idOrchestration())) {
                criarClienteResponses.get(result.idOrchestration()).complete(dto);
            }

        } catch (Exception ex) {
            errors.add(ex.getMessage());
            ok = false;
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
    public CompletableFuture<ClienteCreateResponseDTO> startCriarCliente(ClienteRequestDTO dto) throws Exception {

        var idOrchestration = UUID.randomUUID();

        try {

            ObjectMapper mapper = new ObjectMapper();

            var request = new OrchestrationRequestDTO(
                    idOrchestration,
                    false,
                    List.of(
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_CLIENTE, OrchestrationKeys.CREATE_CLIENTE_COMMAND, mapper.writeValueAsString(dto)),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_GERENTE, OrchestrationKeys.FIND_GERENTE_COMMAND, mapper.writeValueAsString(dto))
                    )
            );

            var completable = new CompletableFuture<ClienteCreateResponseDTO>();
            criarClienteResponses.put(idOrchestration, completable);
            rabbitTemplate.convertSendAndReceive(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

            return completable;

        } catch (Exception ex) {
            throw ex;
        }

    }

}
