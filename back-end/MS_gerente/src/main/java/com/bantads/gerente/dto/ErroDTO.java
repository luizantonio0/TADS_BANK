package com.bantads.gerente.dto;

public record ErroDTO(
        String mensagem,
        String stackTrace,
        int status
) {
}
