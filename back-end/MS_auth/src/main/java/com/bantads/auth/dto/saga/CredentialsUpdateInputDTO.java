package com.bantads.auth.dto.saga;

public record CredentialsUpdateInputDTO(String cpf, String email) {
    public CredentialsUpdateInputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}
