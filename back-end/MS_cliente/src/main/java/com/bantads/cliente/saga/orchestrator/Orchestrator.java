package com.bantads.cliente.saga.orchestrator;

import com.bantads.cliente.saga.dto.OrchestrationResultDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Orchestrator {

    private static final Map<Long, Orchestration> orchestrations = new HashMap<>();

    public static boolean orchestrate(Orchestration orchestration) {
        orchestrations.put(orchestration.getId(), orchestration);

    }

    public static void onResult(OrchestrationResultDTO dto) {

        if(!orchestrations.containsKey(dto.idOrchestration())) {
            // se o map de orchestrations não tiver a key, quer dizer que esse serviço não é o orquestrador dessa operação.
            // portanto esse result deve ser ignorado.
            return;
        }

        var orchestration = orchestrations.get(dto.idOrchestration());

    }


}
