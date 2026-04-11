package com.bantads.conta.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
    @Bean
    public Queue msContaCommandQueue() {
        return new Queue("ms-conta.orchestrate", true);
    }

    @Bean
    public Queue confirmQueue() {
        return new Queue("orchestration.confirm", true);
    }

}