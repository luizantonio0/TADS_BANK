package com.bantads.orquestrador.component;

import com.bantads.shared.dto.OrchestrationRequestDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
import com.bantads.orquestrador.service.OrchestratorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    @Autowired @Lazy
    private OrchestratorService orchestrator;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @RabbitListener(queues = "orchestration.orchestrate")
    public OrchestrationRequestResultDTO consumeOrchestrateRequest(OrchestrationRequestDTO dto) {
        return orchestrator.orchestrate(dto);
    }

}
