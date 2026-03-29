package com.nwt.notificationservice.messaging;

import com.nwt.notificationservice.messaging.events.NotificationFailedEvent;
import com.nwt.notificationservice.messaging.events.NotificationSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${brico.rabbitmq.exchange}")
    private String exchange;

    /**
     * Publishes a success event with routing key "appointment.confirmed".
     * The appointment-service will consume this and mark the appointment as CONFIRMED.
     */
    public void publishSuccess(NotificationSuccessEvent event) {
        log.info("Publishing NotificationSuccessEvent for appointmentId={}, notificationId={}",
                event.getAppointmentId(), event.getNotificationId());
        rabbitTemplate.convertAndSend(exchange, "appointment.confirmed", event);
    }

    /**
     * Publishes a failure event with routing key "appointment.rollback".
     * The appointment-service will consume this and mark the appointment as CANCELLED.
     */
    public void publishFailure(NotificationFailedEvent event) {
        log.warn("Publishing NotificationFailedEvent for appointmentId={}, reason={}",
                event.getAppointmentId(), event.getReason());
        rabbitTemplate.convertAndSend(exchange, "appointment.rollback", event);
    }
}
