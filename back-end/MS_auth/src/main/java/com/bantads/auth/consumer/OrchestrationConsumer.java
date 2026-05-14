package com.bantads.auth.consumer;

import com.bantads.shared.dto.*;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategyFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrchestrationConsumer {

    @Autowired
    private SagaCommandStrategyFactory cmdFactory;

    @Autowired private RabbitTemplate rabbitTemplate;

    @Autowired private RedisTemplate<String, String> redisTemplate;

    @Autowired private AuthService authService;

    @RabbitListener(queues = "ms-auth.orchestration.finished")
    public void onFinished(OrchestrationRequestResultDTO dto) {
        if(authService.isLoginRequest(dto.idOrchestration())) {
            authService.finishLogin(dto);
        }
        if(authService.isLogoutRequest(dto.idOrchestration())) {
            authService.finishLogout(dto);
        }
    }

    @RabbitListener(queues = "ms-auth.command")
    public void onCommand(OrchestrationCommandDTO dto) {
        var strategy = cmdFactory.newCommand(dto.commandType());
        ObjectMapper mapper = new ObjectMapper();
        if (strategy == null) {
             rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(
                     dto.idCommand(),
                     dto.idOrchestration(),
                     "ms-auth",
                     "Nenhuma estratégia para o comando " + dto.commandType(),
                     false,
                    null
             ));
             return;
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

        rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(dto.idCommand(), dto.idOrchestration(), "ms-auth", message, ok, payload));
    }

    @RabbitListener(queues = "ms-auth.orchestration.confirm")
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
