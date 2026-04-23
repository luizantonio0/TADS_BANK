package com.bantads.conta.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
        return new Queue("ms-conta.command", true);
    }

    @Bean
    public TopicExchange confirmExchange() {
        return new TopicExchange("orchestration.confirm");
    }

    @Bean
    public Queue confirmQueue() {
        return new Queue("ms-conta.orchestration.confirm", true);
    }

    @Bean
    public Binding binding(Queue confirmQueue, TopicExchange confirmExchange) {
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with("orchestration.confirm");
    }
}