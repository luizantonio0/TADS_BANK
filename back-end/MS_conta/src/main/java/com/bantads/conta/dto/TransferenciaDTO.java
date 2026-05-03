package com.bantads.conta.dto;

import java.math.BigDecimal;

public record TransferenciaDTO(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor) {}
