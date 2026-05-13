package com.bantads.auth.dto.saga;

public record GetProfileInputDTO(String cpf) {
    public GetProfileInputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}
