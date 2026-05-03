package com.bantads.conta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoDTO(
    LocalDateTime dataHora,
    String operacao,
    String contaOrigem,
    String contaDestino,
    BigDecimal valor,
    String cor
) {}
