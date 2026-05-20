package com.bantads.cliente.dto;

import com.bantads.cliente.model.Cliente;

import java.math.BigDecimal;

public record ClienteResumidoDTO(
    String cpf,
    String email,
    String nome,
    BigDecimal salario,
    String endereco,
    String cidade,
    String estado
) {

    public ClienteResumidoDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
    
    public static ClienteResumidoDTO from(Cliente c) {
        if (c == null) return null;
        return new ClienteResumidoDTO(
            c.getCpf(),
            c.getEmail(),
            c.getNome(),
            c.getSalario(),
            c.getEndereco(),
            c.getCep(),
            c.getCidade()
        );
    }

}
