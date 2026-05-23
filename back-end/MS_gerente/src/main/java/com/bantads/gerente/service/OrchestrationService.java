package com.bantads.gerente.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bantads.gerente.dto.ClienteDTO;
import com.bantads.gerente.dto.GerenteClienteDashboardDTO;
import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.GerenteDashboardDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteCriadoDTO;
import com.bantads.gerente.dto.response.GetContasByGerentesBatchOutputDTO;
import com.bantads.gerente.dto.saga.CredentialsCreateInputDTO;
import com.bantads.gerente.exception.HttpException;
import com.bantads.gerente.exception.InternalServerErrorException;
import com.bantads.gerente.repository.GerenteRepository;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.bantads.shared.dto.OrchestrationRequestDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrchestrationService {
    
    @Autowired private GerenteRepository repository;
    @Autowired private RabbitTemplate rabbitTemplate;

    private Map<UUID, CompletableFuture<GerenteCriadoDTO>> criarGerenteRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<List<GerenteDashboardDTO>>> gerenteDashboardRequests = new HashMap<>();

    public boolean isCriarCliente(UUID id) {
        return criarGerenteRequests.containsKey(id);
    }

    public boolean isGerenteDashboard(UUID id) {
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

    public CompletableFuture<List<GerenteDashboardDTO>> startGerenteDashboard() throws Exception {
        var orchestrationId = UUID.randomUUID();
        var mapper = new ObjectMapper();

        var listaGerentes = repository.findAll().stream().map(c -> c.getCpf()).toList();

        var request = new OrchestrationRequestDTO(orchestrationId, true, List.of(
            new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-conta", "GetContasByGerenteBatch", mapper.writeValueAsString(listaGerentes)),
            new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-cliente", "GetClientesByGerenteBatch", mapper.writeValueAsString(listaGerentes))
        ));
        
        var completable = new CompletableFuture<List<GerenteDashboardDTO>>();
        gerenteDashboardRequests.put(orchestrationId, completable);
        rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

        return completable;
        
    }

    public void finishGerenteDashboard(OrchestrationRequestResultDTO result) {
        CompletableFuture<List<GerenteDashboardDTO>> completableFuture = null;
        try {
            completableFuture = gerenteDashboardRequests.get(result.idOrchestration());
            prepareResult(result, gerenteDashboardRequests, "ms-conta", "ms-cliente");
            
            ObjectMapper mapper = new ObjectMapper();
            var contaOutput = mapper.readValue(result.payloads().get("ms-conta"), new TypeReference<Map<String, GetContasByGerentesBatchOutputDTO>>() {});
            var clientesOutput = mapper.readValue(result.payloads().get("ms-cliente"), new TypeReference<Map<String, List<ClienteDTO>>>() {});

            var listaGerentes = repository.findAll();
            var response = new ArrayList<GerenteDashboardDTO>();

            for (var gerente : listaGerentes) {
                var gerenteDTO = GerenteDTO.from(gerente, false);
                var contaDTO = contaOutput.get(gerente.getCpf());
                var clientesDTO = clientesOutput.get(gerente.getCpf());

                var clientes = new ArrayList<GerenteClienteDashboardDTO>();

                for (var cliente : clientesDTO) {
                    var contaCliente = contaDTO.contas().get(cliente.cpf());
                    clientes.add(new GerenteClienteDashboardDTO(cliente.cpf(), contaCliente.conta(), contaCliente.saldo(), contaCliente.limite(), gerente.getCpf(), cliente.criacao()));
                }

                response.add(new GerenteDashboardDTO(gerenteDTO, clientes, contaDTO.saldoPositivo(), contaDTO.saldoNegativo()));
            }

            completableFuture.complete(response);
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
