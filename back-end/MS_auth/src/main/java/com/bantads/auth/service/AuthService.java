package com.bantads.auth.service;

import com.bantads.auth.document.Credentials;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private CredentialsRepository credentialsRepository;
    private PasswordEncoder encoder;

    public AuthService(CredentialsRepository credentialsRepository, PasswordEncoder encoder) {
        this.credentialsRepository = credentialsRepository;
        this.encoder = encoder;
    }

    public boolean login(String login, String senha) {
        var credentials = credentialsRepository.findByEmail(login);
        return credentials.filter(value -> encoder.matches(senha, value.getPassword())).isPresent();
    }

    public void createCredentials(String email, String cpf, String senha) {
        if(email == null || cpf == null || senha == null || email.trim().isEmpty() || cpf.trim().isEmpty() || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Email, CPF e Senha devem ser preenchidos.");
        }
        if(credentialsRepository.existsById(cpf)) {
            throw new CredentialsAlreadyExistsException();
        }
        var cryptoPw = encoder.encode(senha);
        credentialsRepository.insert(new Credentials(cpf, email, cryptoPw));
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

}
