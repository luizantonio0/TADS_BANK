package com.bantads.auth.dto.saga;

import java.util.UUID;

public record GerenteDTO(
        UUID id,
        String cpf,
        String nome,
        String email,
        String tipo,
        Integer totalClientes
) {}