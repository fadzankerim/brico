package com.nwt.notificationservice.service;

import com.nwt.notificationservice.dto.request.CreateNotificationRequest;
import com.nwt.notificationservice.dto.request.UpdateNotificationStatusRequest;
import com.nwt.notificationservice.dto.response.NotificationResponse;
import com.nwt.notificationservice.exception.ResourceNotFoundException;
import com.nwt.notificationservice.messaging.SagaEventPublisher;
import com.nwt.notificationservice.messaging.events.AppointmentBookedEvent;
import com.nwt.notificationservice.messaging.events.NotificationFailedEvent;
import com.nwt.notificationservice.messaging.events.NotificationSuccessEvent;
import com.nwt.notificationservice.model.Notification;
import com.nwt.notificationservice.model.NotificationStatus;
import com.nwt.notificationservice.model.NotificationType;
import com.nwt.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SagaEventPublisher sagaEventPublisher;

    @Override
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .appointmentId(request.getAppointmentId())
                .type(request.getType())
                .status(NotificationStatus.PENDING)
                .scheduledAt(request.getScheduledAt())
                .message(request.getMessage())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Manual notification created: id={}, userId={}, appointmentId={}",
                saved.getId(), saved.getUserId(), saved.getAppointmentId());
        return NotificationResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        return NotificationResponse.from(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(NotificationResponse::from);
    }

    @Override
    @Transactional
    public NotificationResponse updateNotificationStatus(Long id, UpdateNotificationStatusRequest request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));

        notification.setStatus(request.getStatus());
        if (request.getStatus() == NotificationStatus.SENT) {
            notification.setSentAt(LocalDateTime.now());
        }

        Notification updated = notificationRepository.save(notification);
        log.info("Notification {} status updated to {}", id, request.getStatus());
        return NotificationResponse.from(updated);
    }

    @Override
    @Transactional
    public void createBookingNotification(AppointmentBookedEvent event) {
        String message = buildBookingMessage(event);

        Notification notification = Notification.builder()
                .userId(event.getClientId())
                .appointmentId(event.getAppointmentId())
                .type(NotificationType.APPOINTMENT_BOOKED)
                .status(NotificationStatus.PENDING)
                .scheduledAt(LocalDateTime.now())
                .message(message)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Booking notification created: id={}, appointmentId={}",
                saved.getId(), saved.getAppointmentId());

        NotificationSuccessEvent successEvent = NotificationSuccessEvent.builder()
                .appointmentId(event.getAppointmentId())
                .notificationId(saved.getId())
                .build();
        sagaEventPublisher.publishSuccess(successEvent);
    }

    @Override
    @Transactional
    public void processUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursLater = now.plusHours(2);

        List<Notification> pending = notificationRepository
                .findByStatusAndScheduledAtBetween(NotificationStatus.PENDING, now, twoHoursLater);

        log.info("Scheduler found {} pending notifications due within 2 hours", pending.size());

        for (Notification notification : pending) {
            log.info("Would send email to userId={} for appointmentId={}",
                    notification.getUserId(), notification.getAppointmentId());
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    private String buildBookingMessage(AppointmentBookedEvent event) {
        return String.format(
                "Dear %s, your appointment has been booked for %s. Appointment ID: %d.",
                event.getClientName() != null ? event.getClientName() : "Customer",
                event.getStartTime() != null ? event.getStartTime() : "N/A",
                event.getAppointmentId()
        );
    }
}
