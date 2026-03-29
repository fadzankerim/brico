package com.nwt.appointmentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String APPOINTMENT_EXCHANGE = "brico.appointments";

    // Queues
    public static final String NOTIFICATION_APPOINTMENT_BOOKED_QUEUE = "notification.appointment.booked";
    public static final String APPOINTMENT_CONFIRMED_QUEUE = "appointment.confirmed";
    public static final String APPOINTMENT_ROLLBACK_QUEUE = "appointment.rollback";

    // Routing keys
    public static final String APPOINTMENT_BOOKED_ROUTING_KEY = "appointment.booked";
    public static final String APPOINTMENT_CONFIRMED_ROUTING_KEY = "appointment.confirmed";
    public static final String APPOINTMENT_ROLLBACK_ROUTING_KEY = "appointment.rollback";

    @Bean
    public TopicExchange appointmentExchange() {
        return new TopicExchange(APPOINTMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationAppointmentBookedQueue() {
        return new Queue(NOTIFICATION_APPOINTMENT_BOOKED_QUEUE, true);
    }

    @Bean
    public Queue appointmentConfirmedQueue() {
        return new Queue(APPOINTMENT_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Queue appointmentRollbackQueue() {
        return new Queue(APPOINTMENT_ROLLBACK_QUEUE, true);
    }

    @Bean
    public Binding notificationBookedBinding(Queue notificationAppointmentBookedQueue,
                                              TopicExchange appointmentExchange) {
        return BindingBuilder
                .bind(notificationAppointmentBookedQueue)
                .to(appointmentExchange)
                .with(APPOINTMENT_BOOKED_ROUTING_KEY);
    }

    @Bean
    public Binding appointmentConfirmedBinding(Queue appointmentConfirmedQueue,
                                                TopicExchange appointmentExchange) {
        return BindingBuilder
                .bind(appointmentConfirmedQueue)
                .to(appointmentExchange)
                .with(APPOINTMENT_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding appointmentRollbackBinding(Queue appointmentRollbackQueue,
                                               TopicExchange appointmentExchange) {
        return BindingBuilder
                .bind(appointmentRollbackQueue)
                .to(appointmentExchange)
                .with(APPOINTMENT_ROLLBACK_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
