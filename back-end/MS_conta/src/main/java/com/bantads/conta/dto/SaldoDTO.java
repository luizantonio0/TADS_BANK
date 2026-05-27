package com.bantads.conta.dto;

import java.math.BigDecimal;

public record SaldoDTO(String cliente, String conta, BigDecimal saldo) {
    
}
