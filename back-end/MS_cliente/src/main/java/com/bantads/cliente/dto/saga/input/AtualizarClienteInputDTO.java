package com.bantads.cliente.dto.saga.input;

import java.math.BigDecimal;

import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;

public record AtualizarClienteInputDTO (
    String cpf,
    String nome,
    String email,
    BigDecimal salario,
    String endereco,
    String CEP,
    String cidade,
    String estado
) {

    public static AtualizarClienteInputDTO from(String cpf, AlterarDadosClienteDTO dto) {
        return new AtualizarClienteInputDTO(cpf, dto.nome(), dto.email(), dto.salario(), dto.endereco(), dto.CEP(), dto.cidade(), dto.estado());
    }

}
