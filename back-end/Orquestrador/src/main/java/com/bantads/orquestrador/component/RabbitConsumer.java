package com.bantads.orquestrador.component;

import com.bantads.orquestrador.dto.OrchestrationRequestDTO;
import com.bantads.orquestrador.dto.OrchestrationResultDTO;
import com.bantads.orquestrador.service.OrchestratorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    @Autowired
    private OrchestratorService orchestrator;

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @RabbitListener(queues = "orchestration.orchestrate")
    public void consumeOrchestrateRequest(OrchestrationRequestDTO dto) {
        orchestrator.orchestrate(dto);
    }

    @RabbitListener(queues = "orchestration.result")
    public void consumeOrchestrateResult(OrchestrationResultDTO dto) {
        orchestrator.onResult(dto);
    }

}
