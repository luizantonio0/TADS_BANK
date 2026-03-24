package com.bantads.cliente.saga.orchestrator;

import com.bantads.cliente.saga.dto.OrchestrationResultDTO;

import java.util.HashMap;
import java.util.Map;

// orchestrator só cuida das transações
public class Orchestrator {

    private static final Map<Long, Orchestration> orchestrations = new HashMap<>();
    private static final Map<Long, OrchestratorCommand<?>> pendingCommands = new HashMap<>();

    public static void onResult(OrchestrationResultDTO dto) {

        if(!orchestrations.containsKey(dto.idOrchestration())) {
            // se o map de orchestrations não tiver a key, quer dizer que esse serviço não é o orquestrador dessa operação.
            // portanto esse result deve ser ignorado.
            return;
        }

        var orchestration = orchestrations.get(dto.idOrchestration());

    }


}
