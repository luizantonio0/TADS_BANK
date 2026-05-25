package com.bantads.gerente.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteAtualizadoDTO;
import com.bantads.gerente.dto.saga.CredentialsUpdateInputDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteCriadoDTO;
import com.bantads.gerente.dto.saga.CredentialsCreateInputDTO;
import com.bantads.gerente.exception.HttpException;
import com.bantads.gerente.exception.InternalServerErrorException;
import com.bantads.gerente.repository.GerenteRepository;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.bantads.shared.dto.OrchestrationRequestDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrchestrationService {
    
    private final GerenteRepository repository;
    private final RabbitTemplate rabbitTemplate;

    private Map<UUID, CompletableFuture<GerenteCriadoDTO>> criarGerenteRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<GerenteAtualizadoDTO>> atualizarGerenteRequests = new HashMap<>();

    public OrchestrationService(GerenteRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public boolean isCriarCliente(UUID id) {
        return criarGerenteRequests.containsKey(id);
    }

    private <T> void prepareResult(OrchestrationRequestResultDTO dto, Map<UUID, T> orchMap, String... payloads)
            throws HttpException {
        if (dto == null)
            throw new InternalServerErrorException("Resposta nula do orquestrador");

        if (!orchMap.containsKey(dto.idOrchestration()))
            throw new InternalServerErrorException(
                    "CompletableFuture para idOrchestration " + dto.idOrchestration() + " não encontrado.");

        for (var p : payloads)
            if (!dto.payloads().containsKey(p))
                throw new InternalServerErrorException("Payload " + p + " não encontrado");

        if (dto.failed()) {
            var err = dto.errors().values().iterator().next();
            if (err == null) {
                throw new HttpException(500, "Algo deu errado. Tente novamente mais tarde.");
            }
            throw HttpException.wrap(err.status(), err.message());
        }
    }

    public CompletableFuture<GerenteAtualizadoDTO> startAtualizarGerente(String _cpf, AtualizaGerenteDTO _dto) throws Exception{
        var cpf = _cpf.replaceAll("[^0-9]", "");

        if(!repository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Gerente não encontrado!");
        }

        if(_dto.email() != null && repository.existsByEmail(_dto.email())) {
            throw new IllegalArgumentException("Gerente com email já cadastrado!");
        }

        var dto = new AtualizaGerenteDTO(
                _dto.nome(),
                _dto.email(),
                _dto.senha(),
                _dto.telefone(),
                _dto.tipo(),
                cpf
        );

        var orchestrationId = UUID.randomUUID();
        var mapper = new ObjectMapper();

        var authDTO = new CredentialsUpdateInputDTO(cpf, dto.email(), dto.senha());

        var request = new OrchestrationRequestDTO(orchestrationId, true, List.of(
                new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-auth", "UpdateCredentials", mapper.writeValueAsString(authDTO)),
                new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-gerente", "AtualizarGerente", mapper.writeValueAsString(dto))
        ));

        var completable = new CompletableFuture<GerenteAtualizadoDTO>();
        atualizarGerenteRequests.put(orchestrationId, completable);
        rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

        return completable;
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
        rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

        return completable;
        
    }

    public void finishCriarGerente(OrchestrationRequestResultDTO result) {
        CompletableFuture<GerenteCriadoDTO> completableFuture = null;

        try {

            completableFuture = criarGerenteRequests.get(result.idOrchestration());
            prepareResult(result, criarGerenteRequests, "ms-gerente");
            
            ObjectMapper mapper = new ObjectMapper();
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

}
