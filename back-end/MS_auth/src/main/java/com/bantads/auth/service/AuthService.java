package com.bantads.auth.service;

import com.bantads.auth.document.Credentials;
import com.bantads.auth.dto.TokenClaimsDTO;
import com.bantads.auth.dto.response.LoginResponseDTO;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.repository.CredentialsRepository;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private CredentialsRepository credentialsRepository;
    private PasswordEncoder encoder;
    private Javers javers;

    public AuthService(Javers javers, CredentialsRepository credentialsRepository, PasswordEncoder encoder) {
        this.credentialsRepository = credentialsRepository;
        this.encoder = encoder;
        this.javers = javers;
    }

    public boolean login(String login, String senha) {
        var credentials = credentialsRepository.findByEmail(login);
        return credentials.filter(value -> encoder.matches(senha, value.getPassword())).isPresent();
    }

    public void createCredentials(String email, String cpf, String cryptoPw, String profile) {
        if(email == null || cpf == null || cryptoPw == null || email.trim().isEmpty() || cpf.trim().isEmpty() || cryptoPw.trim().isEmpty()) {
            throw new IllegalArgumentException("Email, CPF e Senha devem ser preenchidos.");
        }
        if(credentialsRepository.existsById(cpf)) {
            throw new CredentialsAlreadyExistsException();
        }
        var creds = new Credentials(cpf, email, cryptoPw, profile);
        credentialsRepository.insert(creds);
        javers.commit("system", creds);
    }

    public void updateCredentials(String cpf, String email) {
        if(email == null || cpf == null || email.trim().isEmpty() || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("Email e CPF devem ser preenchidos.");
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
