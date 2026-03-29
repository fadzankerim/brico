package com.nwt.appointmentservice.messaging;

import com.nwt.appointmentservice.config.RabbitMQConfig;
import com.nwt.appointmentservice.messaging.events.NotificationFailedEvent;
import com.nwt.appointmentservice.model.AppointmentStatus;
import com.nwt.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentRollbackConsumer {

    private final AppointmentRepository appointmentRepository;

    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_ROLLBACK_QUEUE)
    @Transactional
    public void handleNotificationFailed(NotificationFailedEvent event) {
        log.warn("Received NotificationFailedEvent for appointmentId={}, reason={}",
                event.getAppointmentId(), event.getReason());
        try {
            appointmentRepository.findById(event.getAppointmentId()).ifPresentOrElse(
                appointment -> {
                    appointment.setStatus(AppointmentStatus.CANCELLED);
                    appointmentRepository.save(appointment);
                    log.info("Appointment {} rolled back to CANCELLED status due to notification failure",
                            event.getAppointmentId());
                },
                () -> log.warn("Appointment {} not found for rollback", event.getAppointmentId())
            );
        } catch (Exception e) {
            log.error("Error processing NotificationFailedEvent for appointmentId={}: {}",
                    event.getAppointmentId(), e.getMessage(), e);
        }
    }
}
