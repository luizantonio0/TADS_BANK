package com.bantads.conta.dto;

import com.bantads.conta.model.TipoMovimentacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CQRSEventDTO(
    String numeroConta,
    BigDecimal novoSaldo,
    TipoMovimentacao tipo,
    BigDecimal valor,
    LocalDateTime dataHora,
    String contaOrigem,
    String contaDestino
) {}
