package com.bantads.conta.dto;

import java.math.BigDecimal;

public record ContaCreateDTO(String numConta, String cpf, String cpfGerente, BigDecimal limite, BigDecimal saldo) {}
