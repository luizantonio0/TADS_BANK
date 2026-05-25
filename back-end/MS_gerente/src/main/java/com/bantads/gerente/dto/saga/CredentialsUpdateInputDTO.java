package com.bantads.gerente.dto.saga;

public record CredentialsUpdateInputDTO(String cpf, String email, String password) {
    public CredentialsUpdateInputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}
