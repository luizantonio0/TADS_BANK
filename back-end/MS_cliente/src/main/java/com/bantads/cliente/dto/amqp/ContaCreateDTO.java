package com.bantads.cliente.dto.amqp;

import com.bantads.cliente.saga.dto.OrchestratedOperationDTO;

public record ContaCreateDTO (long idOrchestration, int conta, String cpf, String cpfGerente) implements OrchestratedOperationDTO {

    @Override
    public long orchestrationId() {
        return idOrchestration;
    }

}
