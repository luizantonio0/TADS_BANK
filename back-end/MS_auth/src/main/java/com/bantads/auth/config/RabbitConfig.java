package com.bantads.auth.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
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
        return new Queue("ms-auth.command", true);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue("orchestration.result", true);
    }

    @Bean
    public TopicExchange confirmExchange() {
        return new TopicExchange("orchestration.confirm");
    }

    @Bean
    public Queue confirmQueue() {
        return new Queue("ms-auth.orchestration.confirm", true);
    }

    @Bean
    public Binding binding(@Qualifier("confirmQueue") Queue confirmQueue, @Qualifier("confirmExchange") TopicExchange confirmExchange) {
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with("orchestration.confirm");
    }

    @Bean
    public TopicExchange finishedExchange() {
        return new TopicExchange("orchestration.finished");
    }

    @Bean
    public Queue finishedQueue() {
        return new Queue("ms-auth.orchestration.finished", true);
    }

    @Bean
    public Binding finishedBinding(@Qualifier("finishedQueue") Queue finishedQueue, @Qualifier("finishedExchange") TopicExchange finishedExchange) {
        return BindingBuilder.bind(finishedQueue).to(finishedExchange).with("orchestration.finished");
    }

}