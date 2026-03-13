package com.bantads.cliente.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record AprovarClienteDTO(
        //cpf do cliente
        String cliente,
        //numero da conta
        String numero,
        BigDecimal saldo,
        BigDecimal limite,
        String endereco,
        //cpf do gerente
        String gerente,
        //dateTime da criacao da conta
        String criacao
) implements Serializable {
}
