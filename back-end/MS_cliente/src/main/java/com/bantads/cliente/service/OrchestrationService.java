package com.bantads.cliente.service;

import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.AprovarClienteDTO;
import com.bantads.cliente.dto.http.AprovarClienteResponseDTO;
import com.bantads.cliente.dto.http.ClienteCreateResponseDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.dto.saga.GerenteDTO;
import com.bantads.cliente.dto.saga.input.AtualizarClienteInputDTO;
import com.bantads.cliente.dto.saga.input.AtualizarLimiteInputDTO;
import com.bantads.cliente.dto.saga.input.ContaCreateInputDTO;
import com.bantads.cliente.dto.saga.input.CredentialsCreateInputDTO;
import com.bantads.cliente.dto.saga.input.GetGerenteInputDTO;
import com.bantads.cliente.dto.saga.output.*;
import com.bantads.cliente.orchestration.OrchestrationKeys;
import com.bantads.cliente.repository.ClienteRepository;
import com.bantads.shared.dto.OrchestrationCommandDTO;
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
    private final Map<UUID, CompletableFuture<Object>> atualizarClienteResponses = new ConcurrentHashMap<>();

    public boolean isCriarClienteSaga(UUID idOrchestration) {
        return criarClienteResponses.containsKey(idOrchestration);
    }

    public boolean isAprovarClienteSaga(UUID idOrchestration) {
        return aprovarClienteResponses.containsKey(idOrchestration);
    }

    public void assertPayloads(OrchestrationRequestResultDTO result, String... payloads) throws Exception {
        for(var p : payloads) {
            if(!result.payloads().containsKey(p)) {
                throw new IllegalArgumentException("Payload " + p + " não encontrado");
            }
        }
    }

    public CompletableFuture<ClienteCreateResponseDTO> startCriarCliente(ClienteRequestDTO dto) throws Exception {
        var idOrchestration = UUID.randomUUID();

        try {
            ObjectMapper mapper = new ObjectMapper();
            var request = new OrchestrationRequestDTO(
                    idOrchestration,
                    true,
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

    public void finishCriarCliente(OrchestrationRequestResultDTO result) {
        CompletableFuture<ClienteCreateResponseDTO> completableFuture = null;

        try {
            if (result == null) {
                throw new IllegalStateException("Resposta nula do orquestrador");
            }

            completableFuture = criarClienteResponses.get(result.idOrchestration());

            if (completableFuture == null) {
                throw new IllegalStateException("CompletableFuture para idOrchestration " + result.idOrchestration() + " não encontrado.");
            }

            if(result.failed()) {
                throw new IllegalArgumentException(result.errors().values().iterator().next());
            }

            if (!result.payloads().containsKey(OrchestrationKeys.MS_GERENTE)
                    || !result.payloads().containsKey(OrchestrationKeys.MS_CLIENTE)) {
                throw new IllegalArgumentException("Payloads esperados não encontrados");
            }

            ObjectMapper mapper = new ObjectMapper();

            var gerenteOutput = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_GERENTE), DefinirGerenteOutputDTO.class);
            var clienteDTO = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_CLIENTE), CreateClienteOutputDTO.class);

            var clienteOptional = repository.findByCpf(clienteDTO.cpf());
            if(clienteOptional.isEmpty()) {
                throw new IllegalArgumentException("Cliente não encontrado");
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
                    cliente.getEstado().name()
            );

            completableFuture.complete(dto);

        } catch (Exception ex) {
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && criarClienteResponses.containsKey(result.idOrchestration())) {
                criarClienteResponses.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<AprovarClienteResponseDTO> startAprovarCliente(AprovarClienteDTO dto) throws Exception {

        var idOrchestration = UUID.randomUUID();
        var cliente = repository.findByCpf(dto.cpf());
        if(cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado");
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
                    true,
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

        CompletableFuture<AprovarClienteResponseDTO> completableFuture = null;

        try {
            if (result == null) {
                throw new IllegalStateException("Resposta nula do orquestrador");
            }

            completableFuture = aprovarClienteResponses.get(result.idOrchestration());

            if (completableFuture == null) {
                throw new IllegalStateException("CompletableFuture para idOrchestration " + result.idOrchestration() + " não encontrado.");
            }

            if(result.failed()) {
                throw new IllegalArgumentException(result.errors().values().iterator().next());
            }

            assertPayloads(result, OrchestrationKeys.MS_CONTA, OrchestrationKeys.MS_GERENTE, OrchestrationKeys.MS_CLIENTE);

            ObjectMapper mapper = new ObjectMapper();

            var gerenteOutput = mapper.readValue(result.payloads().get(OrchestrationKeys.MS_GERENTE), GerenteDTO.class);
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
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && criarClienteResponses.containsKey(result.idOrchestration())) {
                aprovarClienteResponses.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<Object> startAtualizarCliente(String cpf, AlterarDadosClienteDTO dto) throws Exception {

        var idOrchestration = UUID.randomUUID();
        var cliente = repository.findByCpf(cpf);
        if(cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            var contaDTO = new AtualizarLimiteInputDTO(cpf, dto.salario());
            var clienteDTO = AtualizarClienteInputDTO.from(cpf, dto);

            List<OrchestrationCommandDTO> commands = List.of(
                new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_CLIENTE, OrchestrationKeys.UPDATE_CLIENTE_COMMAND, mapper.writeValueAsString(clienteDTO))
            );

            if(cliente.get().isAprovado()) {
                commands.add(new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), OrchestrationKeys.MS_CONTA, OrchestrationKeys.UPDATE_LIMITE_COMMAND, mapper.writeValueAsString(contaDTO)));
            }

            var request = new OrchestrationRequestDTO(idOrchestration, true, commands);

            var completable = new CompletableFuture<>();
            atualizarClienteResponses.put(idOrchestration, completable);
            rabbitTemplate.convertAndSend(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

            return completable;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Transactional
    public void finishAtualizarCliente(OrchestrationRequestResultDTO result) {
        CompletableFuture<Object> completableFuture = null;

        try {
            if (result == null) {
                throw new IllegalStateException("Resposta nula do orquestrador");
            }

            completableFuture = atualizarClienteResponses.get(result.idOrchestration());

            if (completableFuture == null) {
                throw new IllegalStateException("CompletableFuture para idOrchestration " + result.idOrchestration() + " não encontrado.");
            }

            if(result.failed()) {
                throw new IllegalArgumentException(result.errors().values().iterator().next());
            }

            completableFuture.complete(null);

        } catch (Exception ex) {
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && criarClienteResponses.containsKey(result.idOrchestration())) {
                atualizarClienteResponses.remove(result.idOrchestration());
            }
        }
    }

}
