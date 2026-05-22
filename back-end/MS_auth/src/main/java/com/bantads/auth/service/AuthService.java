package com.bantads.auth.service;

import com.bantads.auth.document.Credentials;
import com.bantads.auth.dto.LoginDTO;
import com.bantads.auth.dto.LoginResponseDTO;
import com.bantads.auth.dto.LoginUsuarioResponseDTO;
import com.bantads.auth.dto.LogoutResponseDTO;
import com.bantads.auth.dto.TokenClaimsDTO;
import com.bantads.auth.dto.saga.AuthResponseDTO;
import com.bantads.auth.dto.saga.ClienteDTO;
import com.bantads.auth.dto.saga.GerenteDTO;
import com.bantads.auth.dto.saga.GetProfileInputDTO;
import com.bantads.auth.exception.BadRequestException;
import com.bantads.auth.exception.HttpException;
import com.bantads.auth.exception.InternalServerErrorException;
import com.bantads.auth.exception.NotFoundException;
import com.bantads.auth.exception.UnauthorizedException;
import com.bantads.auth.orchestration.OrchestrationKeys;
import com.bantads.auth.repository.CredentialsRepository;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.bantads.shared.dto.OrchestrationRequestDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {

    private Map<UUID, CompletableFuture<LoginResponseDTO>> loginsRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<LogoutResponseDTO>> logoutRequests = new HashMap<>();

    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private CredentialsRepository credentialsRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private Javers javers;
    @Autowired private ObjectMapper mapper;

    @Autowired private JwtService jwtService;

    public boolean isLoginRequest(UUID id) {
        return loginsRequests.containsKey(id);
    }

    public boolean isLogoutRequest(UUID id) {
        return logoutRequests.containsKey(id);
    }

    private <T> void prepareResult(OrchestrationRequestResultDTO dto, Map<UUID,T> orchMap, String... payloads) throws HttpException {
        if (dto == null)
            throw new InternalServerErrorException("Resposta nula do orquestrador");
        
        if(!orchMap.containsKey(dto.idOrchestration()))
            throw new InternalServerErrorException("CompletableFuture para idOrchestration " + dto.idOrchestration() + " não encontrado.");
        
        for(var p : payloads)
            if(!dto.payloads().containsKey(p))
                throw new InternalServerErrorException("Payload " + p + " não encontrado");

        if(dto.failed()) {
            var err = dto.errors().values().iterator().next();
            if (err == null) {
                throw new HttpException(500, "Algo deu errado. Tente novamente mais tarde.");
            }
            throw HttpException.wrap(err.status(), err.message());
        }
    }

    public void finishLogout(OrchestrationRequestResultDTO result) {
        CompletableFuture<LogoutResponseDTO> completableFuture = null;

        try {
            completableFuture = logoutRequests.get(result.idOrchestration());
            prepareResult(result, logoutRequests);

            ClienteDTO cliente = result.payloads().containsKey("ms-cliente")
                ? mapper.readValue(result.payloads().get("ms-cliente"), ClienteDTO.class)
                : null;

            GerenteDTO gerente = result.payloads().containsKey("ms-gerente")
                ? mapper.readValue(result.payloads().get("ms-gerente"), GerenteDTO.class)
                : null;

            if(cliente == null && gerente == null) {
                throw new NotFoundException("Usuário não encontrado");
            }            

            var dto = (cliente != null) 
                ? new LogoutResponseDTO(cliente.cpf(), cliente.nome(), cliente.email(), "CLIENTE")
                : new LogoutResponseDTO(gerente.cpf(), gerente.nome(), gerente.email(), "GERENTE");

            completableFuture.complete(dto);

        } catch (Exception ex) {
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && loginsRequests.containsKey(result.idOrchestration())) {
                loginsRequests.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<LogoutResponseDTO> startLogout(String token) throws UnauthorizedException, JsonProcessingException {
        if(token == null || token.isEmpty()) {
            throw new UnauthorizedException("Usuário não está logado");
        }
    
        token = token.replace("Bearer ", "");
        var claims = jwtService.parseToken(token);
        if(claims == null) {
            throw new UnauthorizedException("Usuário não está logado");
        }

        var orchestrationId = UUID.randomUUID();
        var input = new GetProfileInputDTO(claims.cpf());

        OrchestrationCommandDTO command;
        if(claims.profile().equalsIgnoreCase("cliente")) {
            command = new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-cliente", "GetCliente", mapper.writeValueAsString(input));
        } else {
            command = new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-gerente", "GetGerente", mapper.writeValueAsString(input));
        }

        var request = new OrchestrationRequestDTO(orchestrationId, true, List.of(
            command,
            new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-auth", "Logout", token)
        ));
    
        var completable = new CompletableFuture<LogoutResponseDTO>();
        logoutRequests.put(orchestrationId, completable);
        rabbitTemplate.convertAndSend(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

        return completable;

    }

    public void finishLogin(OrchestrationRequestResultDTO result) {
        CompletableFuture<LoginResponseDTO> completableFuture = null;

        try {
            completableFuture = loginsRequests.get(result.idOrchestration());
            prepareResult(result, loginsRequests, "ms-auth");

            ClienteDTO cliente = result.payloads().containsKey("ms-cliente")
                ? mapper.readValue(result.payloads().get("ms-cliente"), ClienteDTO.class)
                : null;

            GerenteDTO gerente = result.payloads().containsKey("ms-gerente")
                ? mapper.readValue(result.payloads().get("ms-gerente"), GerenteDTO.class)
                : null;

            AuthResponseDTO auth = mapper.readValue(result.payloads().get("ms-auth"), AuthResponseDTO.class);

            if(cliente == null && gerente == null) {
                throw new NotFoundException("Nenhum usuário encontrado!");
            }

            var dto = new LoginResponseDTO(
                auth.accessToken(),
                auth.tokenType(),
                auth.profile(),
                new LoginUsuarioResponseDTO(
                    cliente != null ? cliente.nome() : gerente.nome(),
                    cliente != null ? cliente.cpf() : gerente.cpf(),
                    cliente != null ? cliente.email() : gerente.email()
                )
            );

            completableFuture.complete(dto);

        } catch (Exception ex) {
            if (completableFuture != null) {
                completableFuture.completeExceptionally(ex);
            }
        } finally {
            if (result != null && loginsRequests.containsKey(result.idOrchestration())) {
                loginsRequests.remove(result.idOrchestration());
            }
        }
    }

    public CompletableFuture<LoginResponseDTO> startLogin(String login, String senha) throws UnauthorizedException, JsonProcessingException {

        var credentials = credentialsRepository.findByEmail(login);
        if(credentials.isEmpty() || credentials.filter(value -> encoder.matches(senha, value.getPassword())).isEmpty()) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        var orchestrationId = UUID.randomUUID();
        var input = new GetProfileInputDTO(credentials.get().getCpf());

        OrchestrationCommandDTO command;
        if(credentials.get().getProfile().equalsIgnoreCase("cliente")) {
            command = new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-cliente", "GetCliente", mapper.writeValueAsString(input));
        } else {
            command = new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-gerente", "GetGerente", mapper.writeValueAsString(input));
        }

        var request = new OrchestrationRequestDTO(orchestrationId, true, List.of(
            command,
            new OrchestrationCommandDTO(orchestrationId, UUID.randomUUID(), "ms-auth", "Login", mapper.writeValueAsString(new LoginDTO(login, senha)))
        ));
        
        var completable = new CompletableFuture<LoginResponseDTO>();
        loginsRequests.put(orchestrationId, completable);
        rabbitTemplate.convertAndSend(OrchestrationKeys.ORCHESTRATE_QUEUE, request);

        return completable;

    }

    public TokenClaimsDTO auth(String login, String senha) {
        var credentials = credentialsRepository.findByEmail(login);
        if (credentials.isEmpty() || credentials.filter(value -> encoder.matches(senha, value.getPassword())).isEmpty()) {
            return null;
        }
        return new TokenClaimsDTO(credentials.get().getCpf(), credentials.get().getProfile());
    }

    public Credentials createCredentials(String email, String cpf, String cryptoPw, String profile) throws BadRequestException {
        if(email == null || cpf == null || cryptoPw == null || email.trim().isEmpty() || cpf.trim().isEmpty() || cryptoPw.trim().isEmpty()) {
            throw new BadRequestException("Email, CPF e Senha devem ser preenchidos.");
        }
        if(credentialsRepository.existsById(cpf)) {
            throw new BadRequestException("Credenciais já foram definidas para esse usuário.");
        }
        if(credentialsRepository.existsByEmail(email)) {
            throw new BadRequestException("Email já está em uso.");
        }
        var creds = new Credentials(cpf, email, cryptoPw, profile);
        credentialsRepository.insert(creds);
        javers.commit("system", creds);
        return creds;
    }

    public Credentials updateCredentials(String cpf, String email) throws BadRequestException {
        if(email == null || cpf == null || email.trim().isEmpty() || cpf.trim().isEmpty()) {
            throw new BadRequestException("Email e CPF devem ser preenchidos.");
        }
        if(credentialsRepository.existsByEmail(email)) {
            throw new BadRequestException("Email já está em uso.");
        }
        var cred = credentialsRepository.findById(cpf);
        if(cred.isPresent()) {
            cred.get().setEmail(email);
            credentialsRepository.save(cred.get());
        }
        return cred.get();
    }

    public void rollbackCredentials(String id) {
        List<Shadow<Credentials>> shadows = javers.findShadows(
                QueryBuilder.byInstanceId(id, Credentials.class)
                        .limit(2)
                        .build()
        );

        var whitelist = List.of(
                "98574307084",
                "64065268052",
                "23862179060",
                "40501740066",
                "12912861012",
                "09506382000",
                "85733854057",
                "58872160006",
                "76179646090"
            );

        if (shadows.size() > 1) {
            var shadow = shadows.get(1);
            credentialsRepository.save(shadow.get());
        } else {
            var conta = credentialsRepository.findById(id);
            if(conta.isPresent() && !whitelist.contains(conta.get().getCpf())) {
                credentialsRepository.deleteById(id);
            }
        }
    }

}
