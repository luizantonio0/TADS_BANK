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
    public Queue credentialsCreateQueue() {
        return new Queue("ms_auth.credentials.create", true);
    }

    @Bean
    public Queue credentialsUpdateQueue() {
        return new Queue("ms_auth.credentials.update", true);
    }

}