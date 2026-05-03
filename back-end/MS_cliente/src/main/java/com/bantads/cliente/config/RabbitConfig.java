package com.bantads.cliente.config;

import com.bantads.cliente.orchestration.OrchestrationKeys;
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
    public Queue msclienteCommandQueue() {
        return new Queue(OrchestrationKeys.MS_CLIENTE + ".orchestrate", true);
    }

    @Bean
    public TopicExchange confirmExchange() {
        return new TopicExchange("orchestration.confirm");
    }

    @Bean
    public Queue confirmQueue() {
        return new Queue("ms-cliente.orchestration.confirm", true);
    }

    @Bean
    public Binding binding(Queue confirmQueue, TopicExchange confirmExchange) {
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with("orchestration.confirm");
    }

    @Bean
    public TopicExchange finishedExchange() {
        return new TopicExchange("orchestration.finished");
    }

    @Bean
    public Queue finishedQueue() {
        return new Queue("ms-cliente.orchestration.finished", true);
    }

    @Bean
    public Binding finishedBinding(Queue finishedQueue, TopicExchange finishedExchange) {
        return BindingBuilder.bind(finishedQueue).to(finishedExchange).with("orchestration.finished");
    }


}