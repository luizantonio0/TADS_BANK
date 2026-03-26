package com.bantads.auth.configuration;

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
    public Queue commandQueue() {
        return new Queue("ms_auth.command", true);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue("orchestration.result", true);
    }

    @Bean
    public Queue confirmQueue() {
        return new Queue("orchestration.confirm", true);
    }

}