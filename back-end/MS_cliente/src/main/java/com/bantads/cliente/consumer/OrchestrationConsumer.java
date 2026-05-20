package com.bantads.cliente.consumer;

import com.bantads.cliente.service.OrchestrationService;
import com.bantads.shared.dto.*;
import com.bantads.cliente.exception.HttpException;
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
        System.out.println("Recebeu " + dto.idOrchestration());
        if(orchestrationService.isCriarClienteSaga(dto.idOrchestration())) {
            System.out.println("Recebeu response da criação de cliente");
            orchestrationService.finishCriarCliente(dto);
        }
        if(orchestrationService.isAprovarClienteSaga(dto.idOrchestration())) {
            System.out.println("Recebeu response da aprovação de cliente");
            orchestrationService.finishAprovarCliente(dto);
        }
        if(orchestrationService.isAtualizarClienteSaga(dto.idOrchestration())) {
            System.out.println("Recebeu response da atualização de cliente");
            orchestrationService.finishAtualizarCliente(dto);
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
                    new OrchestrationErrorDTO(500, "Nenhuma estratégia para o comando " + dto.commandType()),
                    null
            ));
            return;
        }

        String payload = null;
        OrchestrationErrorDTO message = null;

        try {
            var obj = strategy.handle(dto);
            if(obj != null) {
                payload = mapper.writeValueAsString(obj);
            }
        } catch (HttpException ex) {
            message = new OrchestrationErrorDTO(ex.getStatusCode(), ex.getMessage());
        } catch (Exception ex) {
            message = new OrchestrationErrorDTO(500, ex.getMessage());
        }

        rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(
                dto.idCommand(),
                dto.idOrchestration(),
                "ms-cliente",
                message,
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
