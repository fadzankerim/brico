package com.nwt.appointmentservice.messaging;

import com.nwt.appointmentservice.config.RabbitMQConfig;
import com.nwt.appointmentservice.messaging.events.AppointmentBookedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAppointmentBooked(AppointmentBookedEvent event) {
        log.info("Publishing AppointmentBookedEvent for appointmentId={}", event.getAppointmentId());
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.APPOINTMENT_EXCHANGE,
                    RabbitMQConfig.APPOINTMENT_BOOKED_ROUTING_KEY,
                    event
            );
            log.info("AppointmentBookedEvent published successfully for appointmentId={}", event.getAppointmentId());
        } catch (Exception e) {
            log.error("Failed to publish AppointmentBookedEvent for appointmentId={}: {}",
                    event.getAppointmentId(), e.getMessage(), e);
        }
    }
}
