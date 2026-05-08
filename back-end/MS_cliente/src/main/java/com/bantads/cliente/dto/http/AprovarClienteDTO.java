package com.bantads.cliente.dto.http;

import java.io.Serializable;

public record AprovarClienteDTO(
        String cpf
) implements Serializable { }
