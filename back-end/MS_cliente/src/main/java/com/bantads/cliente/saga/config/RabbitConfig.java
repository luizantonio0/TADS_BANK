package com.bantads.cliente.saga.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue orchestrationCommandQueue() {
        return new Queue("orchestration.command", true);
    }

    @Bean
    public Queue orchestrationReturnQueue() {
        return new Queue("orchestration.return", true);
    }

    @Bean
    public Queue orchestrationConfirmQueue() {
        return new Queue("orchestration.confirm", true);
    }

}