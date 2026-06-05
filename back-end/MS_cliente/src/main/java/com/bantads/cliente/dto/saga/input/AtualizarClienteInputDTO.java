package com.bantads.cliente.dto.saga.input;

import java.math.BigDecimal;

import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;

public record AtualizarClienteInputDTO (
    String cpf,
    String nome,
    String email,
    BigDecimal salario,
    String endereco,
    String cep,
    String cidade,
    String estado,
    String telefone
) {

    public AtualizarClienteInputDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        cep = (cep != null) ? cep.replaceAll("\\D", "") : null;
        telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
    }

    public static AtualizarClienteInputDTO from(String cpf, AlterarDadosClienteDTO dto) {
        return new AtualizarClienteInputDTO(cpf, dto.nome(), dto.email(), dto.salario(), dto.endereco(), dto.cep(), dto.cidade(), dto.estado(), dto.telefone());
    }

}
