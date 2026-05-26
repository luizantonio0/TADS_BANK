package com.bantads.conta.dto.cqrs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bantads.conta.model.Conta;
import com.bantads.conta.model.Movimentacao;

public class CQRSSyncEntity {

    public static record ContaDTO(UUID id, String conta, String cpf, BigDecimal saldo, BigDecimal limite, String cpfGerente, String criacao) {
        public ContaDTO {
            cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        }
        public static ContaDTO from(Conta c) {
            return new ContaDTO(c.getId(), c.getConta(), c.getCpf(), c.getSaldo(), c.getLimite(), c.getCpfGerente(), c.getCriacao().toString());
        }
    }

    public static record MovimentacaoDTO(UUID id, LocalDateTime dataHora, String tipo, BigDecimal valor, String contaOrigem, String contaDestino) {
        public static MovimentacaoDTO from(Movimentacao c) {
            return new MovimentacaoDTO(c.getId(), c.getDataHora(), c.getTipo().name(), c.getValor(), c.getContaOrigem(), c.getContaDestino());
        }
    }

}
