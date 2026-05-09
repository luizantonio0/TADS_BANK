package com.bantads.cliente.consumer;

import com.bantads.cliente.service.OrchestrationService;
import com.bantads.shared.dto.*;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategyFactory;
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
    private ClienteService clienteService;

    @Autowired
    private OrchestrationService orchestrationService;

    @RabbitListener(queues = "ms-cliente.orchestration.finished")
    public void onResult(OrchestrationRequestResultDTO dto) {
        if(orchestrationService.isCriarClienteSaga(dto.idOrchestration())) {
            orchestrationService.finishCriarCliente(dto);
        }
        if(orchestrationService.isAprovarClienteSaga(dto.idOrchestration())) {
            orchestrationService.finishAprovarCliente(dto);
        }
    }

    @RabbitListener(queues = "ms-cliente.command")
    public void onCommand(OrchestrationCommandDTO dto) {
        var strategy = cmdFactory.newCommand(dto.commandType());
        ObjectMapper mapper = new ObjectMapper();
        if (strategy == null) {
            rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(
                    dto.idCommand(),
                    dto.idOrchestration(),
                    "ms-cliente",
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

        rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(
                dto.idCommand(),
                dto.idOrchestration(),
                "ms-cliente",
                message,
                ok,
                payload
        ));
    }

    @RabbitListener(queues = "ms-cliente.orchestration.confirm")
    public void onConfirm(OrchestrationConfirmDTO dto) {

        var redisKey = dto.idOrchestration().toString() + ":touched:cliente";
        var touchedCliente = redisTemplate.opsForValue().getAndDelete(redisKey);

        if(dto.ok() || touchedCliente == null) {
            return;
        }

        try {
            clienteService.rollbackCliente(UUID.fromString(touchedCliente));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
