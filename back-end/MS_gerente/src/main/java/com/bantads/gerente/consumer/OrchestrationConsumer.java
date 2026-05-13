package com.bantads.gerente.consumer;

import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.service.OrchestrationService;
import com.bantads.gerente.strategy.SagaCommandStrategyFactory;
import com.bantads.shared.dto.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
@Slf4j
public class OrchestrationConsumer {

    @Autowired
    private SagaCommandStrategyFactory cmdFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired private GerenteService gerenteService;
    @Autowired private OrchestrationService orchestrationService;

    @RabbitListener(queues = "ms-gerente.orchestration.finished")
    public void onFinished(OrchestrationRequestResultDTO dto) {
        if(orchestrationService.isCriarCliente(dto.idOrchestration())) {
            orchestrationService.finishCriarGerente(dto);
        }
    }

    @RabbitListener(queues = "ms-gerente.command")
    public void onCommand(OrchestrationCommandDTO dto) {
        log.debug("Comando %s (%s) sendo processado", dto.commandType(), dto.idCommand().toString());
        var strategy = cmdFactory.newCommand(dto.commandType());
        ObjectMapper mapper = new ObjectMapper();
        if (strategy == null) {
            log.debug(String.format("Estratégia para o comando %s não encontrada", dto.commandType()));
            rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(
                    dto.idCommand(),
                    dto.idOrchestration(),
                    "ms-gerente",
                    "Nenhuma estratégia para o comando " + dto.commandType(),
                    false,
                    null
            ));
        }

        String payload = null;
        String message = "";
        boolean ok = true;

        try {
            var obj = strategy.handle(dto);
            if(obj != null) {
                payload = mapper.writeValueAsString(obj);
            }
            log.debug(String.format("Comando %s (%s) processado com sucesso", dto.commandType(), dto.idCommand().toString()));
        } catch (Exception ex) {
            ok = false;
            message = ex.getMessage();
            log.debug(String.format("Comando %s (%s) finalizou com erro: %s", dto.commandType(), dto.idCommand().toString(), ex.getMessage()));
        }

        rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(
                dto.idCommand(),
                dto.idOrchestration(),
                "ms-gerente",
                message,
                ok,
                payload)
        );
    }

    @RabbitListener(queues = "ms-gerente.orchestration.confirm")
    public void onConfirm(OrchestrationConfirmDTO dto) {

        var redisKey = dto.idOrchestration().toString() + ":touched:gerente";
        var touched = redisTemplate.opsForValue().getAndDelete(redisKey);

        if(dto.ok() || touched == null) {
            return;
        }

        try {
            gerenteService.rollbackGerente(UUID.fromString(touched));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
