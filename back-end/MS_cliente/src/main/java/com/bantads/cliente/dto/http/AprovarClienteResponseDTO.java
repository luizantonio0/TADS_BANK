package com.bantads.cliente.dto.http;

import java.math.BigDecimal;

public record AprovarClienteResponseDTO(
        String cliente,
        String numero,
        BigDecimal saldo,
        BigDecimal limite,
        String gerente,
        String criacao
) { }
