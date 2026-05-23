package com.bantads.gerente.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContaDTO(String cpf, String conta, BigDecimal saldo, BigDecimal limite, LocalDateTime criacao) {
    public ContaDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}