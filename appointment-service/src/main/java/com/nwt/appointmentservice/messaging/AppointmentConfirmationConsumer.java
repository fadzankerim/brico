package com.nwt.appointmentservice.messaging;

import com.nwt.appointmentservice.config.RabbitMQConfig;
import com.nwt.appointmentservice.messaging.events.NotificationSuccessEvent;
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
public class AppointmentConfirmationConsumer {

    private final AppointmentRepository appointmentRepository;

    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_CONFIRMED_QUEUE)
    @Transactional
    public void handleNotificationSuccess(NotificationSuccessEvent event) {
        log.info("Received NotificationSuccessEvent for appointmentId={}", event.getAppointmentId());
        try {
            appointmentRepository.findById(event.getAppointmentId()).ifPresentOrElse(
                appointment -> {
                    appointment.setStatus(AppointmentStatus.CONFIRMED);
                    appointmentRepository.save(appointment);
                    log.info("Appointment {} status updated to CONFIRMED", event.getAppointmentId());
                },
                () -> log.warn("Appointment {} not found for confirmation", event.getAppointmentId())
            );
        } catch (Exception e) {
            log.error("Error processing NotificationSuccessEvent for appointmentId={}: {}",
                    event.getAppointmentId(), e.getMessage(), e);
        }
    }
}
