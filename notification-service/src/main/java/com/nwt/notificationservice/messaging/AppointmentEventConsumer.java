package com.nwt.notificationservice.messaging;

import com.nwt.notificationservice.messaging.events.AppointmentBookedEvent;
import com.nwt.notificationservice.messaging.events.NotificationFailedEvent;
import com.nwt.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventConsumer {

    private final NotificationService notificationService;
    private final SagaEventPublisher sagaEventPublisher;

    /**
     * Listens for AppointmentBookedEvent from appointment-service (Saga choreography step).
     *
     * On success: notificationService saves the notification and publishes NotificationSuccessEvent.
     * On failure: catches the exception, publishes NotificationFailedEvent, and does NOT rethrow
     *             (to prevent infinite requeue loops).
     */
    @RabbitListener(queues = "${brico.rabbitmq.queue.booked}")
    public void handleAppointmentBooked(AppointmentBookedEvent event) {
        log.info("Received AppointmentBookedEvent for appointmentId={}, clientId={}",
                event.getAppointmentId(), event.getClientId());

        try {
            notificationService.createBookingNotification(event);
            log.info("Saga step SUCCESS: notification created for appointmentId={}", event.getAppointmentId());
        } catch (Exception ex) {
            log.error("Saga step FAILED for appointmentId={}: {}", event.getAppointmentId(), ex.getMessage(), ex);

            NotificationFailedEvent failedEvent = NotificationFailedEvent.builder()
                    .appointmentId(event.getAppointmentId())
                    .reason("Failed to create notification: " + ex.getMessage())
                    .build();

            try {
                sagaEventPublisher.publishFailure(failedEvent);
            } catch (Exception publishEx) {
                log.error("Could not publish NotificationFailedEvent for appointmentId={}: {}",
                        event.getAppointmentId(), publishEx.getMessage(), publishEx);
            }
            // Do NOT rethrow — prevents the message from being requeued indefinitely
        }
    }
}
