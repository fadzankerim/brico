package ba.unsa.etf.nwt.bookingservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAppointmentCreated(AppointmentCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.APPOINTMENT_CREATED_KEY,
                event);
        log.info("Objavljen event appointment.created za appointmentId={}", event.getAppointmentId());
    }

    public void publishNotification(Long userId, String type, String title, String message, Long refId) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.NOTIFICATION_KEY,
                new NotificationEvent(userId, type, title, message, refId));
        log.info("Notifikacija → userId={} type={}", userId, type);
    }
}
