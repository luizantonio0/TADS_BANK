package com.bantads.cliente.dto.saga.output;

public record AprovarClienteOutputDTO(String cpf, String criacao) {
    public AprovarClienteOutputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}
