package com.bantads.gerente.dto;

import java.math.BigDecimal;

public record ContaDTO(String cpf, String conta, BigDecimal saldo, BigDecimal limite, String criacao) {
    public ContaDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}