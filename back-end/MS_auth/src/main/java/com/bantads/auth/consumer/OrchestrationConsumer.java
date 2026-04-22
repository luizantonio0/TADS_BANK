package com.bantads.auth.consumer;

import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;
import com.bantads.auth.dto.orchestration.OrchestrationCommandResultDTO;
import com.bantads.auth.dto.orchestration.OrchestrationConfirmDTO;
import com.bantads.auth.orchestration.OrchestrationKeys;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import com.bantads.auth.strategy.SagaCommandStrategyFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class OrchestrationConsumer {

    @Autowired
    private SagaCommandStrategyFactory cmdFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AuthService authService;

    @RabbitListener(queues = "ms-auth.command")
    public <T> OrchestrationCommandResultDTO consumeCreate(OrchestrationCommandDTO<T> dto) {
        var strategy = (SagaCommandStrategy<T>) cmdFactory.newCommand(dto.commandType());
        ObjectMapper mapper = new ObjectMapper();
        if (strategy == null) {
            return new OrchestrationCommandResultDTO(
                            dto.idCommand(),
                            dto.idOrchestration(),
                            "None strategy found for command " + dto.commandType(),
                            false,
                    null
                    );
        }

        String payload = null;
        String message = "";
        boolean ok = true;

        try {
            var obj = strategy.handle(dto);
            if(obj != null) {
                payload = mapper.writeValueAsString(obj);
            }
        } catch (Exception ex) {
            ok = false;
            message = ex.getMessage();
        }

        return new OrchestrationCommandResultDTO(dto.idCommand(), dto.idOrchestration(), message, ok, payload);
    }

    @RabbitListener(queues = OrchestrationKeys.CONFIRM_QUEUE)
    public void consumeConfirm(OrchestrationConfirmDTO dto) {

        var redisKey = dto.idOrchestration().toString() + ":touched:credentials";
        var touched = redisTemplate.opsForValue().getAndDelete(redisKey);

        if(dto.ok() || touched == null) {
            return;
        }

        try {
            authService.rollbackCredentials(touched);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
