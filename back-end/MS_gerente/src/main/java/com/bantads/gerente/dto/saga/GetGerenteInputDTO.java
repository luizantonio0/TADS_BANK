package com.bantads.gerente.dto.saga;

public record GetGerenteInputDTO(String cpf) {
    public GetGerenteInputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    } 
}
