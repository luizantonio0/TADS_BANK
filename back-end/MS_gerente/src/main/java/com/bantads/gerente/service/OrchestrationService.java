package com.bantads.gerente.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteAtualizadoDTO;
import com.bantads.gerente.dto.saga.CredentialsUpdateInputDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteCriadoDTO;
import com.bantads.gerente.dto.saga.AlterarGerenteDTO;
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
    
    private final GerenteService gerenteService;
    private final GerenteRepository repository;
    private final RabbitTemplate rabbitTemplate;

    private Map<UUID, CompletableFuture<GerenteCriadoDTO>> criarGerenteRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<GerenteAtualizadoDTO>> atualizarGerenteRequests = new HashMap<>();

    public OrchestrationService(GerenteService gerenteService, GerenteRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.gerenteService = gerenteService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public boolean isCriarGerente(UUID id) {
        return criarGerenteRequests.containsKey(id);
    }

    public boolean isAtualizarGerente(UUID id) {
        return atualizarGerenteRequests.containsKey(id);
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

    public CompletableFuture<GerenteAtualizadoDTO> startAtualizarGerente(String cpf, AtualizaGerenteDTO dto) throws Exception{

        var gerente = repository.findByCpf(cpf).orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado!"));
        var atualizarLogin = (dto.senha() != null && !dto.senha().isBlank()) 
            || (dto.email() != null && !dto.email().isBlank() && !dto.email().equalsIgnoreCase(gerente.getEmail()));

        if(!atualizarLogin) {
            var ger = this.gerenteService.updateByCpf(cpf, dto);
            return CompletableFuture.completedFuture(new GerenteAtualizadoDTO(ger));
        }

        if(dto.email() != null && repository.existsByEmail(dto.email())) {
            var donoEmail = repository.findByEmail(dto.email()).get();
            if(!donoEmail.getCpf().equalsIgnoreCase(cpf)) {
                throw new HttpException(409, "Gerente com email já cadastrado!");
            }
        }

        var orchestrationId = UUID.randomUUID();
        var mapper = new ObjectMapper();

        var authDTO = new CredentialsUpdateInputDTO(cpf, dto.email(), dto.senha());

        var cmdAuth = new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-auth", "UpdateCredentials", mapper.writeValueAsString(authDTO));
        var cmdGerente = new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-gerente", "AtualizarGerente", mapper.writeValueAsString(dto));

        var request = new OrchestrationRequestDTO(orchestrationId, true, atualizarLogin ? List.of(cmdAuth, cmdGerente) : List.of(cmdGerente));
                
        var completable = new CompletableFuture<GerenteAtualizadoDTO>();
        atualizarGerenteRequests.put(orchestrationId, completable);
        rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

        return completable;
    }

    public void finishAtualizarGerente(OrchestrationRequestResultDTO result) {
        CompletableFuture<GerenteAtualizadoDTO> completableFuture = null;

        try {

            completableFuture = atualizarGerenteRequests.get(result.idOrchestration());
            prepareResult(result, atualizarGerenteRequests, "ms-gerente");
            
            ObjectMapper mapper = new ObjectMapper();
            GerenteDTO gerenteDTO = mapper.readValue(result.payloads().get("ms-gerente"), GerenteDTO.class);

            var dto = new GerenteAtualizadoDTO(
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
                atualizarGerenteRequests.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<GerenteCriadoDTO> startCriarGerentePasso1(CriaGerenteDTO dto) throws Exception {
        var idOrchestration = UUID.randomUUID();
        var cpf = dto.cpf().replaceAll("[^0-9]", "");
        if(repository.existsByCpf(cpf)) {
            throw new HttpException(409, "Gerente com CPF já cadastrado!");
        }

        if(repository.existsByEmail(dto.email())) {
            throw new HttpException(409, "Gerente com email já cadastrado!");
        }

        var orchestrationId = UUID.randomUUID();
        var mapper = new ObjectMapper();
        var authDTO = new CredentialsCreateInputDTO(dto.email(), cpf, dto.senha(), dto.tipo().getNome());

        var commands = List.of(
            new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-auth", "CreateCredentials", mapper.writeValueAsString(authDTO)),
            new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-gerente", "CreateGerente", mapper.writeValueAsString(dto))
        );

        var gerComMaisClientes = repository.findGerentesComMaisClientes();
        if(gerComMaisClientes.size() > 0 && gerComMaisClientes.get(0).getClientes().size() > 1) {
            if(gerComMaisClientes.size() == 1) {
                
                var gerenteComMaisClientes = gerComMaisClientes.get(0);
                var cpfCliente = gerenteComMaisClientes.getClientes().get(0);
                
                gerenteComMaisClientes.decrementTotalClientes();
                gerenteComMaisClientes.getClientes().remove(cpfCliente);
                repository.save(gerenteComMaisClientes);

                var dtoAlterarGerente = new AlterarGerenteDTO(cpfCliente, cpf);

                commands.add(new OrchestrationCommandDTO(
                        idOrchestration,
                        UUID.randomUUID(),
                        "ms-conta",
                        "AlterarGerenteConta",
                        mapper.writeValueAsString(dtoAlterarGerente)));

                commands.add(new OrchestrationCommandDTO(
                        idOrchestration,
                        UUID.randomUUID(),
                        "ms-cliente",
                        "AlterarGerenteCliente",
                        mapper.writeValueAsString(dtoAlterarGerente)));

            } else {

                var menorSaldoCpf = new AtomicReference<String>("");
                for(var g : gerComMaisClientes) {
                    if(menorSaldoCpf.get().equals("")) {
                        menorSaldoCpf.set(g.getCpf());
                        continue;
                    }
                    var atual = dto.saldosNegativos().get(menorSaldoCpf.get());
                    var comparando = dto.saldosNegativos().get(g.getCpf());
                    if(comparando.compareTo(atual) < 0) {
                        menorSaldoCpf.set(g.getCpf());
                    }
                }

                var menorSaldo = gerComMaisClientes.stream().filter(x -> x.getCpf().equals(menorSaldoCpf.get())).findFirst().get();
                var cpfCliente = menorSaldo.getClientes().get(0);
                
                menorSaldo.decrementTotalClientes();
                menorSaldo.getClientes().remove(cpfCliente);
                repository.save(menorSaldo);

                var dtoAlterarGerente = new AlterarGerenteDTO(cpfCliente, cpf);
                commands.add(new OrchestrationCommandDTO(
                    idOrchestration,
                    UUID.randomUUID(),
                    "ms-conta",
                    "AlterarGerenteConta",
                    mapper.writeValueAsString(dtoAlterarGerente)));

                commands.add(new OrchestrationCommandDTO(
                        idOrchestration,
                        UUID.randomUUID(),
                        "ms-cliente",
                        "AlterarGerenteCliente",
                        mapper.writeValueAsString(dtoAlterarGerente)));
            }
        }

        var request = new OrchestrationRequestDTO(orchestrationId, false, commands);

        var completable = new CompletableFuture<GerenteCriadoDTO>();
        criarGerenteRequests.put(orchestrationId, completable);
        rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

        return completable;
    }

    public void finishCriarGerente(OrchestrationRequestResultDTO result) {
        CompletableFuture<GerenteCriadoDTO> completableFuture = null;
        // manda as novas atribuições de contas
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
