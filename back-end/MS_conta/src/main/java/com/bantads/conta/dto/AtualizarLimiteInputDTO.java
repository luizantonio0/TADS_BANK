package com.bantads.conta.dto;

import java.math.BigDecimal;

public record AtualizarLimiteInputDTO(String cpf, BigDecimal salario) {
    
}
