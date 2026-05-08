package com.bantads.cliente.service;

import com.bantads.cliente.dto.AprovarClienteDTO;
import com.bantads.cliente.dto.AprovarClienteResponseDTO;
import com.bantads.cliente.dto.ClienteCreateResponseDTO;
import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.dto.saga.input.ContaCreateInputDTO;
import com.bantads.cliente.dto.saga.input.CredentialsCreateInputDTO;
import com.bantads.cliente.dto.saga.input.GetGerenteInputDTO;
import com.bantads.cliente.dto.saga.output.*;
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

import java.util.*;
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

    public void assertPayloads(OrchestrationRequestResultDTO result, String... payloads) throws Exception {
        for(var p : payloads) {
            if(!result.payloads().containsKey(p)) {
                throw new Exception("Payload " + p + " não encontrado");
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
            rabbitTemplate.convertAndSend(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

            return completable;

        } catch (Exception ex) {
            throw ex;
        }

    }

    @Transactional
    public void finishCriarCliente(OrchestrationRequestResultDTO result) {

        var ok = true;
        var errors = new ArrayList<String>();
        CompletableFuture<ClienteCreateResponseDTO> completableFuture = null;

        try {
            if (result == null) {
                throw new Exception("Resposta nula do orquestrador");
            }

            completableFuture = criarClienteResponses.get(result.idOrchestration());

            if (completableFuture == null) {
                throw new Exception("CompletableFuture para idOrchestration " + result.idOrchestration() + " não encontrado.");
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

            completableFuture.complete(dto);

        } catch (Exception ex) {
            errors.add(ex.getMessage());
            ok = false;
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if(result != null) {
                rabbitTemplate.convertAndSend(
                        "orchestration.confirm",
                        "orchestration.confirm",
                        new OrchestrationConfirmDTO(result.idOrchestration(), String.join(",", errors), ok)
                );
            }
            if (result != null && criarClienteResponses.containsKey(result.idOrchestration())) {
                criarClienteResponses.remove(result.idOrchestration());
            }
        }
    }

    @Transactional
    public CompletableFuture<AprovarClienteResponseDTO> startAprovarCliente(AprovarClienteDTO dto) throws Exception {

        var idOrchestration = UUID.randomUUID();
        var cliente = repository.findByCpf(dto.cpf());
        if(cliente.isEmpty()) {
            throw new Exception("Cliente não encontrado");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            Random random = new Random();
            int numero = 1000 + random.nextInt(9000);

            var contaDTO = new ContaCreateInputDTO(dto.cpf(), cliente.get().getSalario());
            var authDTO = new CredentialsCreateInputDTO(cliente.get().getEmail(), dto.cpf(), numero + "");
            var gerenteDTO = new GetGerenteInputDTO(cliente.get().getIdGerente());

            var request = new OrchestrationRequestDTO(
                    idOrchestration,
                    false,
                    List.of(
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_CONTA, OrchestrationKeys.CREATE_CONTA_COMMAND, mapper.writeValueAsString(contaDTO)),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_AUTH, OrchestrationKeys.CREATE_CREDENTIALS_COMMAND, mapper.writeValueAsString(authDTO)),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_GERENTE, OrchestrationKeys.GET_GERENTE_COMMAND, mapper.writeValueAsString(gerenteDTO)),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_CLIENTE, OrchestrationKeys.APPROVE_CLIENTE_COMMAND, mapper.writeValueAsString(dto))
                    )
            );

            var completable = new CompletableFuture<AprovarClienteResponseDTO>();
            aprovarClienteResponses.put(idOrchestration, completable);
            rabbitTemplate.convertAndSend(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

            return completable;

        } catch (Exception ex) {
            throw ex;
        }
    }

    @Transactional
    public void finishAprovarCliente(OrchestrationRequestResultDTO result) {

        var ok = true;
        var errors = new ArrayList<String>();
        CompletableFuture<AprovarClienteResponseDTO> completableFuture = null;

        try {
            if (result == null) {
                throw new Exception("Resposta nula do orquestrador");
            }

            completableFuture = aprovarClienteResponses.get(result.idOrchestration());

            if (completableFuture == null) {
                throw new Exception("CompletableFuture para idOrchestration " + result.idOrchestration() + " não encontrado.");
            }

            if(result.failed()) {
                errors.addAll(result.errors().values());
                ok = false;
            }

            assertPayloads(result, OrchestrationKeys.MS_CONTA, OrchestrationKeys.MS_GERENTE, OrchestrationKeys.MS_CLIENTE);

            ObjectMapper mapper = new ObjectMapper();

            var gerenteOutput = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_GERENTE), GetGerenteOutputDTO.class);
            var contaOutput = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_CONTA), ContaCreateOutputDTO.class);
            var clienteOutput = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_CLIENTE), AprovarClienteOutputDTO.class);

            var dto = new AprovarClienteResponseDTO(
                    clienteOutput.cpf(),
                    contaOutput.numero(),
                    contaOutput.saldo(),
                    contaOutput.limite(),
                    gerenteOutput.nome(),
                    clienteOutput.criacao()
            );

            completableFuture.complete(dto);

        } catch (Exception ex) {
            errors.add(ex.getMessage());
            ok = false;
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if(result != null) {
                rabbitTemplate.convertAndSend(
                        "orchestration.confirm",
                        "orchestration.confirm",
                        new OrchestrationConfirmDTO(result.idOrchestration(), String.join(",", errors), ok)
                );
            }
            if (result != null && criarClienteResponses.containsKey(result.idOrchestration())) {
                aprovarClienteResponses.remove(result.idOrchestration());
            }
        }
    }

}
