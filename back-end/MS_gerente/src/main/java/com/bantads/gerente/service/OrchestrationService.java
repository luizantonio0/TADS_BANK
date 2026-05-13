package com.bantads.gerente.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteCriadoDTO;
import com.bantads.gerente.dto.saga.CredentialsCreateInputDTO;
import com.bantads.gerente.orchestration.OrchestrationKeys;
import com.bantads.gerente.repository.GerenteRepository;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.bantads.shared.dto.OrchestrationRequestDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrchestrationService {
    
    @Autowired private GerenteRepository repository;
    @Autowired private RabbitTemplate rabbitTemplate;

    private Map<UUID, CompletableFuture<GerenteCriadoDTO>> criarGerenteRequests = new HashMap<>();

    public boolean isCriarCliente(UUID id) {
        return criarGerenteRequests.containsKey(id);
    }

    public void assertPayloads(OrchestrationRequestResultDTO result, String... payloads) throws Exception {
        for(var p : payloads) {
            if(!result.payloads().containsKey(p)) {
                throw new IllegalArgumentException("Payload " + p + " não encontrado");
            }
        }
    }

    public void finishCriarGerente(OrchestrationRequestResultDTO result) {
        CompletableFuture<GerenteCriadoDTO> completableFuture = null;

        try {
            if (result == null) {
                throw new IllegalStateException("Resposta nula do orquestrador");
            }

            completableFuture = criarGerenteRequests.get(result.idOrchestration());

            if (completableFuture == null) {
                throw new IllegalStateException("CompletableFuture para idOrchestration " + result.idOrchestration() + " não encontrado.");
            }

            if(result.failed()) {
                throw new IllegalArgumentException(result.errors().values().iterator().next());
            }

            ObjectMapper mapper = new ObjectMapper();

            assertPayloads(result, "ms-gerente");

            GerenteDTO gerenteDTO = mapper.readValue(result.payloads().get("ms-gerente"), GerenteDTO.class);

            var dto = new GerenteCriadoDTO(
                gerenteDTO.cpf(),
                gerenteDTO.nome(),
                gerenteDTO.email(),
                gerenteDTO.tipo()
            );

            completableFuture.complete(dto);

        } catch (Exception ex) {
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && criarGerenteRequests.containsKey(result.idOrchestration())) {
                criarGerenteRequests.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<GerenteCriadoDTO> startCriarGerente(CriaGerenteDTO dto) throws Exception {

        var cpf = dto.cpf().replaceAll("[^0-9]", "");
        if(repository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Gerente com CPF já cadastrado!");
        }

        if(repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Gerente com email já cadastrado!");
        }

        var orchestrationId = UUID.randomUUID();
        var mapper = new ObjectMapper();

        var authDTO = new CredentialsCreateInputDTO(dto.email(), cpf, dto.senha(), dto.tipo().getNome());

        var request = new OrchestrationRequestDTO(orchestrationId, true, List.of(
            new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-auth", "CreateCredentials", mapper.writeValueAsString(authDTO)),
            new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-gerente", "CreateGerente", mapper.writeValueAsString(dto))
        ));
        
        var completable = new CompletableFuture<GerenteCriadoDTO>();
        criarGerenteRequests.put(orchestrationId, completable);
        rabbitTemplate.convertAndSend(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

        return completable;
        
    }

}
