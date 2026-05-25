package com.bantads.conta.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SaldoGerenteDTO (@JsonProperty("saldo_positivo") BigDecimal saldoPositivo, @JsonProperty("saldo_negativo") BigDecimal saldoNegativo){
}
