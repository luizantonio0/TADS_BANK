package com.bantads.auth.dto;

public record LoginUsuarioResponseDTO(String nome, String cpf, String email) {
    public LoginUsuarioResponseDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}