package com.bantads.auth.dto.saga;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteDTO(
    UUID id,
    String cpf,
    String email,
    String nome,
    String telefone,
    BigDecimal salario,
    String endereco,
    String cep,
    String cidade,
    String estado,
    boolean aprovado,
    UUID idGerente,
    LocalDateTime criacao
) {}
