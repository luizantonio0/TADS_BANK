package com.bantads.conta.dto;

import java.math.BigDecimal;
import com.bantads.conta.model.Conta;

public record ContaDTO(String cpf, String conta, BigDecimal saldo, BigDecimal limite, String criacao) {
    public ContaDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }

    public static ContaDTO from(Conta conta) {
        return new ContaDTO(conta.getCpf(), conta.getConta(), conta.getSaldo(), conta.getLimite(), conta.getCriacao().toString());
    }
}