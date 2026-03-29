package com.nwt.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${brico.rabbitmq.exchange}")
    private String exchange;

    @Value("${brico.rabbitmq.queue.booked}")
    private String bookedQueue;

    @Value("${brico.rabbitmq.queue.confirmed}")
    private String confirmedQueue;

    @Value("${brico.rabbitmq.queue.rollback}")
    private String rollbackQueue;

    // ---- Exchange ----

    @Bean
    public TopicExchange appointmentExchange() {
        return ExchangeBuilder.topicExchange(exchange)
                .durable(true)
                .build();
    }

    // ---- Queues ----

    @Bean
    public Queue notificationBookedQueue() {
        return QueueBuilder.durable(bookedQueue).build();
    }

    @Bean
    public Queue appointmentConfirmedQueue() {
        return QueueBuilder.durable(confirmedQueue).build();
    }

    @Bean
    public Queue appointmentRollbackQueue() {
        return QueueBuilder.durable(rollbackQueue).build();
    }

    // ---- Bindings ----

    @Bean
    public Binding bookedBinding(Queue notificationBookedQueue, TopicExchange appointmentExchange) {
        return BindingBuilder
                .bind(notificationBookedQueue)
                .to(appointmentExchange)
                .with("appointment.booked");
    }

    @Bean
    public Binding confirmedBinding(Queue appointmentConfirmedQueue, TopicExchange appointmentExchange) {
        return BindingBuilder
                .bind(appointmentConfirmedQueue)
                .to(appointmentExchange)
                .with("appointment.confirmed");
    }

    @Bean
    public Binding rollbackBinding(Queue appointmentRollbackQueue, TopicExchange appointmentExchange) {
        return BindingBuilder
                .bind(appointmentRollbackQueue)
                .to(appointmentExchange)
                .with("appointment.rollback");
    }

    // ---- Message Converter ----

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
}
