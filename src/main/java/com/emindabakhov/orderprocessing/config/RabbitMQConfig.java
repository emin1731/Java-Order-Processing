package com.emindabakhov.orderprocessing.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
    @Value("${app.rabbitmq.queue}")
    private String queue;
    @Value("${app.rabbitmq.exchange}")
    private String exchange;
    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;
    @Bean
    public Queue orderQueue() {
        return new Queue(queue, true); // durable = true
    }
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(exchange);
    }
    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(routingKey);
    }
    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter(); // serialize as JSON
    }
}