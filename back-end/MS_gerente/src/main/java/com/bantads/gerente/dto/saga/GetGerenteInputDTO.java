package com.bantads.gerente.dto.saga;

public record GetGerenteInputDTO(String cpf, String cpfCliente) {
    public GetGerenteInputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        cpfCliente = (cpfCliente != null) ? cpfCliente.replaceAll("\\D", "") : null;
    } 
}
