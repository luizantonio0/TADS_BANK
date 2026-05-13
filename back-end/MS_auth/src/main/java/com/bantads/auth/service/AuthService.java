package com.bantads.auth.service;

import com.bantads.auth.document.Credentials;
import com.bantads.auth.dto.LoginDTO;
import com.bantads.auth.dto.LoginResponseDTO;
import com.bantads.auth.dto.LoginUsuarioResponseDTO;
import com.bantads.auth.dto.TokenClaimsDTO;
import com.bantads.auth.dto.saga.AuthResponseDTO;
import com.bantads.auth.dto.saga.ClienteDTO;
import com.bantads.auth.dto.saga.GerenteDTO;
import com.bantads.auth.dto.saga.GetProfileInputDTO;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.orchestration.OrchestrationKeys;
import com.bantads.auth.repository.CredentialsRepository;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.bantads.shared.dto.OrchestrationRequestDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
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
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {

    private Map<UUID, CompletableFuture<LoginResponseDTO>> loginsRequests = new HashMap<>();

    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private CredentialsRepository credentialsRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private Javers javers;
    @Autowired private ObjectMapper mapper;

    public boolean isLoginSaga(UUID id) {
        return loginsRequests.containsKey(id);
    }

     public void assertPayloads(OrchestrationRequestResultDTO result, String... payloads) throws Exception {
        for(var p : payloads) {
            if(!result.payloads().containsKey(p)) {
                throw new IllegalArgumentException("Payload " + p + " não encontrado");
            }
        }
    }

    public void finishLogin(OrchestrationRequestResultDTO result) {
        CompletableFuture<LoginResponseDTO> completableFuture = null;

        try {
            if (result == null) {
                throw new IllegalStateException("Resposta nula do orquestrador");
            }

            completableFuture = loginsRequests.get(result.idOrchestration());

            if (completableFuture == null) {
                throw new IllegalStateException("CompletableFuture para idOrchestration " + result.idOrchestration() + " não encontrado.");
            }

            if(result.failed()) {
                throw new IllegalArgumentException(result.errors().values().iterator().next());
            }

            assertPayloads(result, "ms-auth");

            ClienteDTO cliente = result.payloads().containsKey("ms-cliente")
                ? mapper.readValue(result.payloads().get("ms-cliente"), ClienteDTO.class)
                : null;

            GerenteDTO gerente = result.payloads().containsKey("ms-gerente")
                ? mapper.readValue(result.payloads().get("ms-gerente"), GerenteDTO.class)
                : null;

            AuthResponseDTO auth = mapper.readValue(result.payloads().get("ms-auth"), AuthResponseDTO.class);

            if(cliente == null && gerente == null) {
                throw new NoSuchElementException("Nenhum usuário encontrado!");
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

    public CompletableFuture<LoginResponseDTO> startLogin(String login, String senha) throws Exception {

        var credentials = credentialsRepository.findByEmail(login);
        if(credentials.isEmpty() || credentials.filter(value -> encoder.matches(senha, value.getPassword())).isEmpty()) {
            throw new IllegalArgumentException("Credenciais inválidas");
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

    public void createCredentials(String email, String cpf, String cryptoPw, String profile) {
        if(email == null || cpf == null || cryptoPw == null || email.trim().isEmpty() || cpf.trim().isEmpty() || cryptoPw.trim().isEmpty()) {
            throw new IllegalArgumentException("Email, CPF e Senha devem ser preenchidos.");
        }
        if(credentialsRepository.existsById(cpf)) {
            throw new CredentialsAlreadyExistsException();
        }
        if(credentialsRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está em uso.");
        }
        var creds = new Credentials(cpf, email, cryptoPw, profile);
        credentialsRepository.insert(creds);
        javers.commit("system", creds);
    }

    public void updateCredentials(String cpf, String email) {
        if(email == null || cpf == null || email.trim().isEmpty() || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("Email e CPF devem ser preenchidos.");
        }
        if(credentialsRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está em uso.");
        }
        credentialsRepository.findById(cpf).ifPresent(usuario -> {
            usuario.setEmail(email);
            credentialsRepository.save(usuario);
        });
    }

    public void rollbackCredentials(String cpf) {
        List<Shadow<Credentials>> shadows = javers.findShadows(
                QueryBuilder.byInstanceId(cpf, Credentials.class)
                        .limit(2)
                        .build()
        );

        if (shadows.size() >= 2) {
            credentialsRepository.save(shadows.get(1).get());
        } else {
            credentialsRepository.deleteById(cpf);
        }
    }

}
