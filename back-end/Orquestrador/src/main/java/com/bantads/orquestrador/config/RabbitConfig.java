package com.bantads.orquestrador.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // fila onde os serviços mandam pedidos de orquestração
    @Bean
    public Queue orchestrationCommandQueue() {
        return new Queue("orchestration.orchestrate", true);
    }

    // fila os serviços mandam resultados dos comandos
    @Bean
    public Queue orchestrationReturnQueue() {
        return new Queue("orchestration.result", true);
    }

    // fila onde o orquestrador manda confirmação para os serviços
    @Bean
    public Queue orchestrationConfirmQueue() {
        return new Queue("orchestration.confirm", true);
    }

    @Bean
    public Queue msAuthCommandQueue() {
        return new Queue("ms-auth.command", true);
    }

    @Bean
    public Queue msGerenteCommandQueue() {
        return new Queue("ms-gerente.command", true);
    }

    @Bean
    public Queue msContaCommandQueue() {
        return new Queue("ms-conta.command", true);
    }

    @Bean
    public Queue msClienteCommandQueue() {
        return new Queue("ms-cliente.command", true);
    }


}