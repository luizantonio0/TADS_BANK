package com.bantads.cliente.dto.saga.output;

public record CreateClienteOutputDTO(String cpf) {
    public CreateClienteOutputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}
