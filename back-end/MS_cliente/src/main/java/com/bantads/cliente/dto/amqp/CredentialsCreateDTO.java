package com.bantads.cliente.dto.amqp;

public record CredentialsCreateDTO(long id, String email, String cpf, String password) {
}
