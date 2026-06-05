package com.bantads.cliente.dto.saga.input;

public record CredentialsUpdateInputDTO(String cpf, String email, String password, boolean changePassword) {
    public CredentialsUpdateInputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}
