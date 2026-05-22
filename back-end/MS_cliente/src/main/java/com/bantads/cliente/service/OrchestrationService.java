package com.bantads.cliente.service;

import com.bantads.cliente.dto.ClienteDTO;
import com.bantads.cliente.dto.RejeitarClienteRequestDTO;
import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.AprovarClienteDTO;
import com.bantads.cliente.dto.http.AprovarClienteResponseDTO;
import com.bantads.cliente.dto.http.ClienteCreateResponseDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.dto.saga.ContaDTO;
import com.bantads.cliente.dto.saga.GerenteDTO;
import com.bantads.cliente.dto.saga.input.AtualizarClienteInputDTO;
import com.bantads.cliente.dto.saga.input.AtualizarLimiteInputDTO;
import com.bantads.cliente.dto.saga.input.ContaCreateInputDTO;
import com.bantads.cliente.dto.saga.input.CredentialsCreateInputDTO;
import com.bantads.cliente.dto.saga.input.GetGerenteInputDTO;
import com.bantads.cliente.dto.saga.input.RejeitarClienteInputDTO;
import com.bantads.cliente.dto.saga.output.*;
import com.bantads.cliente.exception.BadRequestException;
import com.bantads.cliente.exception.HttpException;
import com.bantads.cliente.exception.InternalServerErrorException;
import com.bantads.cliente.exception.NotFoundException;
import com.bantads.cliente.exception.UnauthorizedException;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.repository.ClienteRepository;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.bantads.shared.dto.OrchestrationRequestDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
import jakarta.transaction.Transactional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrchestrationService {

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final Map<UUID, CompletableFuture<AprovarClienteResponseDTO>> aprovarClienteResponses = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<ClienteCreateResponseDTO>> criarClienteResponses = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<List<ClienteDTO>>> getClienteGerenteResponses = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<List<ClienteDTO>>> getMelhoresClientesResponses = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<ClienteDTO>> getClienteResponses = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<Object>> rejeitarClienteResponses = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<Object>> atualizarClienteResponses = new ConcurrentHashMap<>();

    public boolean isCriarClienteSaga(UUID idOrchestration) {
        return criarClienteResponses.containsKey(idOrchestration);
    }

    public boolean isAprovarClienteSaga(UUID idOrchestration) {
        return aprovarClienteResponses.containsKey(idOrchestration);
    }

    public boolean isAtualizarClienteSaga(UUID idOrchestration) {
        return atualizarClienteResponses.containsKey(idOrchestration);
    }

    public boolean isRejeitarClienteSaga(UUID idOrchestration) {
        return rejeitarClienteResponses.containsKey(idOrchestration);
    }

    public boolean isClienteGerenteResponses(UUID idOrchestration) {
        return getClienteGerenteResponses.containsKey(idOrchestration);
    }

    public boolean isMelhoresClientesResponses(UUID idOrchestration) {
        return getMelhoresClientesResponses.containsKey(idOrchestration);
    }

    public boolean isGetClienteResponses(UUID idOrchestration) {
        return getClienteResponses.containsKey(idOrchestration);
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

    public CompletableFuture<ClienteCreateResponseDTO> startCriarCliente(ClienteRequestDTO dto) throws Exception {
        var idOrchestration = UUID.randomUUID();

        if (repository.existsByCpf(dto.cpf().trim())) {
            throw new BadRequestException("CPF já cadastrado!");
        }

        if (repository.existsByEmail(dto.email().trim())) {
            throw new BadRequestException("Email já está em uso!");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            var request = new OrchestrationRequestDTO(
                    idOrchestration,
                    true,
                    List.of(
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-cliente",
                                    "CreateCliente", mapper.writeValueAsString(dto)),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-gerente",
                                    "IncrementClientesGerente", mapper.writeValueAsString(dto))));

            var completable = new CompletableFuture<ClienteCreateResponseDTO>();
            criarClienteResponses.put(idOrchestration, completable);
            rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

            return completable;

        } catch (Exception ex) {
            throw ex;
        }

    }

    public void finishCriarCliente(OrchestrationRequestResultDTO result) {
        CompletableFuture<ClienteCreateResponseDTO> completableFuture = null;

        try {
            completableFuture = criarClienteResponses.get(result.idOrchestration());
            prepareResult(result, criarClienteResponses, "ms-gerente", "ms-cliente");

            ObjectMapper mapper = new ObjectMapper();

            var gerenteOutput = mapper.readValue(result.payloads().get("ms-gerente"), DefinirGerenteOutputDTO.class);
            var clienteDTO = mapper.readValue(result.payloads().get("ms-cliente"), CreateClienteOutputDTO.class);

            var clienteOptional = repository.findByCpf(clienteDTO.cpf().replaceAll("[^0-9]", ""));
            ;
            if (clienteOptional.isEmpty()) {
                throw new NotFoundException("Cliente não encontrado");
            }

            var cliente = clienteOptional.get();
            cliente.setCpfGerente(gerenteOutput.cpf());
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
                    cliente.getEstado().name());

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

    public CompletableFuture<AprovarClienteResponseDTO> startAprovarCliente(String cpfGerente, AprovarClienteDTO dto)
            throws Exception {

        var idOrchestration = UUID.randomUUID();
        var cliente = repository.findByCpf(dto.cpf());
        if (cliente.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado");
        }

        if (cliente.get().getCpfGerente() == null || !cliente.get().getCpfGerente().equalsIgnoreCase(cpfGerente)) {
            throw new UnauthorizedException("Você não tem permissão para isso.");
        }

        if (cliente.get().isAprovado()) {
            throw new BadRequestException("Cliente já está aprovado.");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            var senha = new Random().nextInt(9000) + 1000 + "";

            var contaDTO = new ContaCreateInputDTO(dto.cpf(), cliente.get().getSalario());
            var authDTO = new CredentialsCreateInputDTO(cliente.get().getEmail(), dto.cpf(), senha, "CLIENTE");

            var request = new OrchestrationRequestDTO(
                    idOrchestration,
                    true,
                    List.of(
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-conta", "CreateConta", mapper.writeValueAsString(contaDTO)),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-auth", "CreateCredentials", mapper.writeValueAsString(authDTO)),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-gerente", "GetGerente", cliente.get().getCpfGerente()),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-cliente", "ApproveCliente", mapper.writeValueAsString(dto))));

            var completable = new CompletableFuture<AprovarClienteResponseDTO>();
            aprovarClienteResponses.put(idOrchestration, completable);
            rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

            return completable;

        } catch (Exception ex) {
            throw ex;
        }
    }

    @Transactional
    public void finishAprovarCliente(OrchestrationRequestResultDTO result) {

        CompletableFuture<AprovarClienteResponseDTO> completableFuture = null;

        try {
            completableFuture = aprovarClienteResponses.get(result.idOrchestration());
            prepareResult(result, aprovarClienteResponses, "ms-conta", "ms-gerente", "ms-cliente");

            ObjectMapper mapper = new ObjectMapper();

            var gerenteOutput = mapper.readValue(result.payloads().get("ms-gerente"), GerenteDTO.class);
            var contaOutput = mapper.readValue(result.payloads().get("ms-conta"),
                    ContaCreateOutputDTO.class);
            var clienteOutput = mapper.readValue(result.payloads().get("ms-cliente"),
                    AprovarClienteOutputDTO.class);

            var dto = new AprovarClienteResponseDTO(
                    clienteOutput.cpf(),
                    contaOutput.numero(),
                    contaOutput.saldo(),
                    contaOutput.limite(),
                    gerenteOutput.nome(),
                    clienteOutput.criacao());

            completableFuture.complete(dto);

        } catch (Exception ex) {
            ex.printStackTrace();
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && result.idOrchestration() != null) {
                aprovarClienteResponses.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<Object> startAtualizarCliente(String cpf, AlterarDadosClienteDTO dto) throws Exception {

        var idOrchestration = UUID.randomUUID();
        var cliente = repository.findByCpf(cpf);
        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            var contaDTO = new AtualizarLimiteInputDTO(cpf, dto.salario());
            var clienteDTO = AtualizarClienteInputDTO.from(cpf, dto);

            List<OrchestrationCommandDTO> commands = List.of(
                    new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-cliente",
                            "UpdateCliente", mapper.writeValueAsString(clienteDTO)));

            if (cliente.get().isAprovado()) {
                commands.add(new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-conta",
                        "UpdateLimite", mapper.writeValueAsString(contaDTO)));
            }

            var request = new OrchestrationRequestDTO(idOrchestration, true, commands);

            var completable = new CompletableFuture<>();
            atualizarClienteResponses.put(idOrchestration, completable);
            rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

            return completable;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Transactional
    public void finishAtualizarCliente(OrchestrationRequestResultDTO result) {
        CompletableFuture<?> completableFuture = null;

        try {
            completableFuture = atualizarClienteResponses.get(result.idOrchestration());
            prepareResult(result, atualizarClienteResponses);

            completableFuture.complete(null);

        } catch (Exception ex) {
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && result.idOrchestration() != null) {
                atualizarClienteResponses.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<Object> startRejeitarCliente(String cpfGerente, String cpfCliente,
            RejeitarClienteRequestDTO dto) throws Exception {

        var idOrchestration = UUID.randomUUID();
        var cliente = repository.findByCpf(cpfCliente);
        if (cliente.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado");
        }

        if (cliente.get().getCpfGerente() == null || !cliente.get().getCpfGerente().equalsIgnoreCase(cpfGerente)) {
            throw new UnauthorizedException("Você não tem permissão para isso.");
        }

        if (cliente.get().isAprovado()) {
            throw new BadRequestException("Cliente já está aprovado.");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            var gerenteDTO = new GetGerenteInputDTO(cliente.get().getCpfGerente());
            var clienteDTO = new RejeitarClienteInputDTO(cpfCliente, dto.motivo());

            var request = new OrchestrationRequestDTO(
                    idOrchestration,
                    true,
                    List.of(
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-gerente",
                                    "DecrementClientesGerente", mapper.writeValueAsString(gerenteDTO)),
                            new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-cliente",
                                    "RejectCliente", mapper.writeValueAsString(clienteDTO))));

            var completable = new CompletableFuture<>();
            rejeitarClienteResponses.put(idOrchestration, completable);
            rabbitTemplate.convertAndSend("orchestration.orchestrate", request);

            return completable;

        } catch (Exception ex) {
            throw ex;
        }
    }

    @Transactional
    public void finishRejeitarCliente(OrchestrationRequestResultDTO result) {
        CompletableFuture<?> completableFuture = null;
        try {
            completableFuture = rejeitarClienteResponses.get(result.idOrchestration());
            prepareResult(result, rejeitarClienteResponses);
            completableFuture.complete(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && result.idOrchestration() != null) {
                aprovarClienteResponses.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<List<ClienteDTO>> startConsultaClienteGerente(String cpfGerente, String nome) {
        var idOrchestration = UUID.randomUUID();
        var clientes = repository.findByGerente(cpfGerente, nome);

        if (clientes.isEmpty())
            return CompletableFuture.completedFuture(List.of());

        var completable = new CompletableFuture<List<ClienteDTO>>();
        ObjectMapper mapper = new ObjectMapper();
        var cpfs = clientes.stream().map(Cliente::getCpf).toList();

        var request = new OrchestrationRequestDTO(
                idOrchestration,
                true,
                List.of(
                        new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-conta",
                                "GetContaBatch", mapper.writeValueAsString(cpfs)),
                        new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-cliente",
                                "GetClienteBatch", mapper.writeValueAsString(cpfs))));

        getClienteGerenteResponses.put(idOrchestration, completable);
        rabbitTemplate.convertAndSend("orchestration.orchestrate", request);
        return completable;
    }

    @Transactional
    public void finishConsultaClientesGerente(OrchestrationRequestResultDTO result) {

        CompletableFuture<List<ClienteDTO>> completableFuture = null;

        try {
            completableFuture = getClienteGerenteResponses.get(result.idOrchestration());
            prepareResult(result, getClienteGerenteResponses, "ms-conta", "ms-cliente");

            ObjectMapper mapper = new ObjectMapper();

            var mapCliente = mapper.readValue(result.payloads().get("ms-cliente"),
                    new TypeReference<Map<String, ClienteDTO>>() {
                    });
            var mapConta = mapper.readValue(result.payloads().get("ms-conta"),
                    new TypeReference<Map<String, ContaDTO>>() {
                    });

            var clientes = new ArrayList<ClienteDTO>();

            for (var entry : mapCliente.entrySet()) {
                var cliente = entry.getValue();
                var conta = mapConta.get(entry.getKey());
                clientes.add(new ClienteDTO(
                        cliente.cpf(),
                        cliente.nome(),
                        cliente.telefone(),
                        cliente.email(),
                        cliente.salario(),
                        conta == null ? new BigDecimal(0) : conta.saldo(),
                        conta == null ? new BigDecimal(0) : conta.limite(),
                        cliente.endereco(),
                        cliente.cidade(),
                        cliente.estado(),
                        true,
                        null,
                        null,
                        null,
                        null,
                        null));

            }
            clientes.sort(Comparator.comparing(ClienteDTO::nome));
            completableFuture.complete(clientes);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && result.idOrchestration() != null) {
                getClienteGerenteResponses.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<List<ClienteDTO>> startMelhoresClientes() {
        var idOrchestration = UUID.randomUUID();
        var completable = new CompletableFuture<List<ClienteDTO>>();
        var request = new OrchestrationRequestDTO(
                idOrchestration,
                true,
                List.of(
                        new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-conta",
                                "GetMelhoresContas", null)));

        getMelhoresClientesResponses.put(idOrchestration, completable);
        rabbitTemplate.convertAndSend("orchestration.orchestrate", request);
        return completable;
    }

    @Transactional
    public void finishMelhoresClientes(OrchestrationRequestResultDTO result) {

        CompletableFuture<List<ClienteDTO>> completableFuture = null;

        try {
            completableFuture = getMelhoresClientesResponses.get(result.idOrchestration());
            prepareResult(result, getMelhoresClientesResponses, "ms-conta");

            ObjectMapper mapper = new ObjectMapper();

            var contasArray = mapper.readValue(result.payloads().get("ms-conta"),
                    new TypeReference<List<ContaDTO>>() {
                    });

            var clientes = repository.findByCpfIn(contasArray.stream().map(ContaDTO::cpf).toList())
                    .stream().collect(Collectors.toMap(c -> c.getCpf(), c->c));

            var clienteList = new ArrayList<ClienteDTO>();

            for (var conta : contasArray) {
                var cliente = clientes.get(conta.cpf());
                clienteList.add(
                    new ClienteDTO(
                        cliente.getCpf(),
                        cliente.getNome(),
                        cliente.getTelefone(),
                        cliente.getEmail(),
                        cliente.getSalario(),
                        conta == null ? new BigDecimal(0) : conta.saldo(),
                        conta == null ? new BigDecimal(0) : conta.limite(),
                        cliente.getEndereco(),
                        cliente.getCidade(),
                        cliente.getEstado() != null ? cliente.getEstado().name() : null,
                        true,
                        null,
                        null,
                        null,
                        null,
                        null)
                    );

            }
            completableFuture.complete(clienteList);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && result.idOrchestration() != null) {
                getMelhoresClientesResponses.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<ClienteDTO> startGetCliente(String cpf) throws HttpException {
        var idOrchestration = UUID.randomUUID();
        var completable = new CompletableFuture<ClienteDTO>();

        var cliente = repository.findByCpf(cpf);
        if (cliente.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado");
        }

        if(!cliente.get().isAprovado()) {
            throw new HttpException(409, "Cliente ainda não foi aprovado");
        }

        var request = new OrchestrationRequestDTO(
                idOrchestration,
                true,
                List.of(
                        new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-conta", "GetConta", cpf),
                        new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-cliente", "GetCliente", cpf),
                        new OrchestrationCommandDTO(idOrchestration, UUID.randomUUID(), "ms-gerente", "GetGerente", cliente.get().getCpfGerente())
                )
            );

        getClienteResponses.put(idOrchestration, completable);
        rabbitTemplate.convertAndSend("orchestration.orchestrate", request);
        return completable;
    }

    @Transactional
    public void finishGetCliente(OrchestrationRequestResultDTO result) {

        CompletableFuture<ClienteDTO> completableFuture = null;

        try {
            completableFuture = getClienteResponses.get(result.idOrchestration());
            prepareResult(result, getClienteResponses, "ms-conta", "ms-cliente", "ms-gerente");

            ObjectMapper mapper = new ObjectMapper();

            var conta = mapper.readValue(result.payloads().get("ms-conta"), ContaDTO.class);
            var gerente = mapper.readValue(result.payloads().get("ms-gerente"), GerenteDTO.class);
            var cliente = mapper.readValue(result.payloads().get("ms-cliente"), ClienteDTO.class);

            completableFuture.complete(new ClienteDTO(
                cliente.cpf(),
                cliente.nome(),
                cliente.telefone(),
                cliente.email(),
                cliente.salario(),
                conta.saldo(),
                conta.limite(),
                cliente.endereco(),
                cliente.cidade(),
                cliente.estado(),
                cliente.aprovado(),
                cliente.cpfGerente(),
                gerente.nome(),
                gerente.email(),
                conta.conta(),
                cliente.criacao()
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && result.idOrchestration() != null) {
                getMelhoresClientesResponses.remove(result.idOrchestration());
            }
        }
    }

}
