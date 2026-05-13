package com.bantads.cliente.dto.saga.input;

public record GetClienteInputDTO(String cpf) {
    public GetClienteInputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}
