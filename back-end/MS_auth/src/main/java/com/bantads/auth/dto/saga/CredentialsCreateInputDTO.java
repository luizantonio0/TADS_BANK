package com.bantads.auth.dto.saga;

public record CredentialsCreateInputDTO(String email, String cpf, String password, String profile) {
}
