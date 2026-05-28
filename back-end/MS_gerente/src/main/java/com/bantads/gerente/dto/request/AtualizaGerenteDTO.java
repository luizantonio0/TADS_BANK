package com.bantads.gerente.dto.request;

public record AtualizaGerenteDTO(
        String cpf,
        String nome,
        String email,
        String senha
) {}
