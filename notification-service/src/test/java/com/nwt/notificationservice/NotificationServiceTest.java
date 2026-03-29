package com.nwt.notificationservice;

import com.nwt.notificationservice.dto.request.CreateNotificationRequest;
import com.nwt.notificationservice.dto.request.UpdateNotificationStatusRequest;
import com.nwt.notificationservice.dto.response.NotificationResponse;
import com.nwt.notificationservice.exception.ResourceNotFoundException;
import com.nwt.notificationservice.messaging.SagaEventPublisher;
import com.nwt.notificationservice.messaging.events.AppointmentBookedEvent;
import com.nwt.notificationservice.messaging.events.NotificationSuccessEvent;
import com.nwt.notificationservice.model.Notification;
import com.nwt.notificationservice.model.NotificationStatus;
import com.nwt.notificationservice.model.NotificationType;
import com.nwt.notificationservice.repository.NotificationRepository;
import com.nwt.notificationservice.service.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SagaEventPublisher sagaEventPublisher;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        sampleNotification = Notification.builder()
                .id(1L)
                .userId(10L)
                .appointmentId(100L)
                .type(NotificationType.APPOINTMENT_BOOKED)
                .status(NotificationStatus.PENDING)
                .message("Your appointment has been booked.")
                .scheduledAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ---- createNotification ----

    @Test
    @DisplayName("createNotification: saves entity and returns mapped response")
    void createNotification_savesAndReturnsResponse() {
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .userId(10L)
                .appointmentId(100L)
                .type(NotificationType.APPOINTMENT_BOOKED)
                .message("Test message")
                .scheduledAt(LocalDateTime.now().plusHours(2))
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(sampleNotification);

        NotificationResponse response = notificationService.createNotification(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(10L);
        assertThat(response.getAppointmentId()).isEqualTo(100L);
        assertThat(response.getType()).isEqualTo(NotificationType.APPOINTMENT_BOOKED);
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.PENDING);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("createNotification: sets status to PENDING regardless of request")
    void createNotification_alwaysSetsPendingStatus() {
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .userId(10L)
                .appointmentId(100L)
                .type(NotificationType.APPOINTMENT_REMINDER_24H)
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(sampleNotification);

        notificationService.createNotification(request);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.PENDING);
    }

    // ---- getNotificationById ----

    @Test
    @DisplayName("getNotificationById: returns response when found")
    void getNotificationById_returnsResponseWhenFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(sampleNotification));

        NotificationResponse response = notificationService.getNotificationById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("getNotificationById: throws ResourceNotFoundException when not found")
    void getNotificationById_throwsWhenNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getNotificationById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ---- getNotificationsByUserId ----

    @Test
    @DisplayName("getNotificationsByUserId: returns paginated results")
    void getNotificationsByUserId_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Notification> notificationPage =
                new PageImpl<>(List.of(sampleNotification), pageable, 1);

        when(notificationRepository.findByUserId(10L, pageable)).thenReturn(notificationPage);

        Page<NotificationResponse> result = notificationService.getNotificationsByUserId(10L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("getNotificationsByUserId: returns empty page for unknown user")
    void getNotificationsByUserId_returnsEmptyForUnknownUser() {
        Pageable pageable = PageRequest.of(0, 10);
        when(notificationRepository.findByUserId(999L, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<NotificationResponse> result = notificationService.getNotificationsByUserId(999L, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // ---- updateNotificationStatus ----

    @Test
    @DisplayName("updateNotificationStatus: updates status and sets sentAt when SENT")
    void updateNotificationStatus_setsStatusAndSentAt() {
        Notification mutableNotification = Notification.builder()
                .id(1L)
                .userId(10L)
                .appointmentId(100L)
                .type(NotificationType.APPOINTMENT_BOOKED)
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(mutableNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateNotificationStatusRequest request = UpdateNotificationStatusRequest.builder()
                .status(NotificationStatus.SENT)
                .build();

        NotificationResponse response = notificationService.updateNotificationStatus(1L, request);

        assertThat(response.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(mutableNotification.getSentAt()).isNotNull();
    }

    @Test
    @DisplayName("updateNotificationStatus: updates to FAILED without setting sentAt")
    void updateNotificationStatus_failedDoesNotSetSentAt() {
        Notification mutableNotification = Notification.builder()
                .id(1L)
                .userId(10L)
                .appointmentId(100L)
                .type(NotificationType.APPOINTMENT_BOOKED)
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(mutableNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateNotificationStatusRequest request = UpdateNotificationStatusRequest.builder()
                .status(NotificationStatus.FAILED)
                .build();

        NotificationResponse response = notificationService.updateNotificationStatus(1L, request);

        assertThat(response.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(mutableNotification.getSentAt()).isNull();
    }

    @Test
    @DisplayName("updateNotificationStatus: throws ResourceNotFoundException when notification not found")
    void updateNotificationStatus_throwsWhenNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        UpdateNotificationStatusRequest request = UpdateNotificationStatusRequest.builder()
                .status(NotificationStatus.SENT)
                .build();

        assertThatThrownBy(() -> notificationService.updateNotificationStatus(999L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---- createBookingNotification (Saga) ----

    @Test
    @DisplayName("createBookingNotification: saves APPOINTMENT_BOOKED notification and publishes success event")
    void createBookingNotification_savesAndPublishesSuccess() {
        AppointmentBookedEvent event = AppointmentBookedEvent.builder()
                .appointmentId(200L)
                .clientId(20L)
                .hairdresserId(5L)
                .serviceId(2L)
                .salonId(3L)
                .startTime("2026-04-10T14:30:00")
                .clientEmail("charlie@example.com")
                .clientName("Charlie")
                .build();

        Notification savedNotification = Notification.builder()
                .id(50L)
                .userId(20L)
                .appointmentId(200L)
                .type(NotificationType.APPOINTMENT_BOOKED)
                .status(NotificationStatus.PENDING)
                .message("Dear Charlie, your appointment has been booked for 2026-04-10T14:30:00. Appointment ID: 200.")
                .scheduledAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        notificationService.createBookingNotification(event);

        // Verify notification saved
        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notifCaptor.capture());
        Notification capturedNotif = notifCaptor.getValue();
        assertThat(capturedNotif.getUserId()).isEqualTo(20L);
        assertThat(capturedNotif.getAppointmentId()).isEqualTo(200L);
        assertThat(capturedNotif.getType()).isEqualTo(NotificationType.APPOINTMENT_BOOKED);
        assertThat(capturedNotif.getStatus()).isEqualTo(NotificationStatus.PENDING);

        // Verify success event published
        ArgumentCaptor<NotificationSuccessEvent> eventCaptor =
                ArgumentCaptor.forClass(NotificationSuccessEvent.class);
        verify(sagaEventPublisher).publishSuccess(eventCaptor.capture());
        NotificationSuccessEvent successEvent = eventCaptor.getValue();
        assertThat(successEvent.getAppointmentId()).isEqualTo(200L);
        assertThat(successEvent.getNotificationId()).isEqualTo(50L);

        // Verify no failure published
        verify(sagaEventPublisher, never()).publishFailure(any());
    }

    // ---- processUpcomingReminders ----

    @Test
    @DisplayName("processUpcomingReminders: marks PENDING notifications as SENT")
    void processUpcomingReminders_marksPendingNotificationsAsSent() {
        Notification pending = Notification.builder()
                .id(1L)
                .userId(10L)
                .appointmentId(100L)
                .type(NotificationType.APPOINTMENT_REMINDER_1H)
                .status(NotificationStatus.PENDING)
                .scheduledAt(LocalDateTime.now().plusMinutes(30))
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findByStatusAndScheduledAtBetween(
                eq(NotificationStatus.PENDING), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(pending));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.processUpcomingReminders();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(captor.getValue().getSentAt()).isNotNull();
    }

    @Test
    @DisplayName("processUpcomingReminders: does nothing when no PENDING notifications found")
    void processUpcomingReminders_doesNothingWhenEmpty() {
        when(notificationRepository.findByStatusAndScheduledAtBetween(
                eq(NotificationStatus.PENDING), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        notificationService.processUpcomingReminders();

        verify(notificationRepository, never()).save(any());
    }
}
