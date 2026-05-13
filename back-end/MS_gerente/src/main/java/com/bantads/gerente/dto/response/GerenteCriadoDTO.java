package com.bantads.gerente.dto.response;

public record GerenteCriadoDTO(String cpf, String nome, String email, String tipo) {
    public GerenteCriadoDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}