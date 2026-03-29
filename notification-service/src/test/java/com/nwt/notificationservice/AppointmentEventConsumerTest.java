package com.nwt.notificationservice;

import com.nwt.notificationservice.messaging.AppointmentEventConsumer;
import com.nwt.notificationservice.messaging.SagaEventPublisher;
import com.nwt.notificationservice.messaging.events.AppointmentBookedEvent;
import com.nwt.notificationservice.messaging.events.NotificationFailedEvent;
import com.nwt.notificationservice.messaging.events.NotificationSuccessEvent;
import com.nwt.notificationservice.model.Notification;
import com.nwt.notificationservice.model.NotificationStatus;
import com.nwt.notificationservice.model.NotificationType;
import com.nwt.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AppointmentEventConsumerTest {

        @Autowired
        private AppointmentEventConsumer appointmentEventConsumer;

        @Autowired
        private NotificationRepository notificationRepository;

        @MockBean
        private RabbitTemplate rabbitTemplate;

        @BeforeEach
        void setUp() {
                notificationRepository.deleteAll();
        }

        @Test
        @DisplayName("Saga success path: notification is persisted and success event is published")
        void handleAppointmentBooked_success_persistsNotificationAndPublishesSuccessEvent() {
                AppointmentBookedEvent event = AppointmentBookedEvent.builder()
                                .appointmentId(42L)
                                .clientId(7L)
                                .hairdresserId(3L)
                                .serviceId(1L)
                                .salonId(5L)
                                .startTime("2026-04-01T10:00:00")
                                .clientEmail("alice@example.com")
                                .clientName("Alice")
                                .build();

                // Act
                appointmentEventConsumer.handleAppointmentBooked(event);

                // Assert: notification was persisted
                List<Notification> notifications = notificationRepository.findAll();
                assertThat(notifications).hasSize(1);

                Notification saved = notifications.get(0);
                assertThat(saved.getUserId()).isEqualTo(7L);
                assertThat(saved.getAppointmentId()).isEqualTo(42L);
                assertThat(saved.getType()).isEqualTo(NotificationType.APPOINTMENT_BOOKED);
                assertThat(saved.getStatus()).isEqualTo(NotificationStatus.PENDING);
                assertThat(saved.getMessage()).contains("Alice");
                assertThat(saved.getCreatedAt()).isNotNull();

                // Assert: success event published to "appointment.confirmed" routing key
                verify(rabbitTemplate, times(1))
                                .convertAndSend(anyString(), eq("appointment.confirmed"),
                                                any(NotificationSuccessEvent.class));

                // Assert: no failure event published
                verify(rabbitTemplate, never())
                                .convertAndSend(anyString(), eq("appointment.rollback"), any(Object.class));
        }

        @Test
        @DisplayName("Saga failure path: on DB error, failure event is published and no exception is thrown")
        void handleAppointmentBooked_failure_publishesFailureEventAndDoesNotThrow() {
                // Use a spy on the repository to simulate a DB failure
                // We achieve this by mocking RabbitTemplate to throw on the success publish,
                // which exercises the catch block. But a cleaner approach is to mock the
                // service.
                // Here we test via a broken event (null appointmentId triggers NPE in message
                // builder
                // gracefully — but real failure requires a mocked repo).
                //
                // We'll test by wiring a separate consumer that uses a mock service.
                // For the integration test we force the failure via the SagaEventPublisher
                // mock.

                // Build an event with a null clientId to trigger NullPointerException inside
                // the service
                // when it tries to set userId = event.getClientId() (autoboxing null Long)
                AppointmentBookedEvent brokenEvent = AppointmentBookedEvent.builder()
                                .appointmentId(99L)
                                .clientId(null) // will cause NullPointerException during entity save
                                .hairdresserId(1L)
                                .serviceId(1L)
                                .salonId(1L)
                                .startTime("2026-04-01T11:00:00")
                                .clientEmail("broken@example.com")
                                .clientName("Broken")
                                .build();

                // Act — must NOT throw
                org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                                () -> appointmentEventConsumer.handleAppointmentBooked(brokenEvent));

                // Assert: failure event was published to "appointment.rollback"
                verify(rabbitTemplate, times(1))
                                .convertAndSend(anyString(), eq("appointment.rollback"),
                                                any(NotificationFailedEvent.class));

                // Assert: no success event published
                verify(rabbitTemplate, never())
                                .convertAndSend(anyString(), eq("appointment.confirmed"), any(Object.class));
        }

        @Test
        @DisplayName("Saga success path: NotificationSuccessEvent carries correct appointmentId and notificationId")
        void handleAppointmentBooked_successEvent_carriesCorrectIds() {
                AppointmentBookedEvent event = AppointmentBookedEvent.builder()
                                .appointmentId(55L)
                                .clientId(10L)
                                .hairdresserId(2L)
                                .serviceId(1L)
                                .salonId(1L)
                                .startTime("2026-05-01T09:00:00")
                                .clientEmail("bob@example.com")
                                .clientName("Bob")
                                .build();

                appointmentEventConsumer.handleAppointmentBooked(event);

                // Capture the published event
                org.mockito.ArgumentCaptor<NotificationSuccessEvent> captor = org.mockito.ArgumentCaptor
                                .forClass(NotificationSuccessEvent.class);

                verify(rabbitTemplate).convertAndSend(anyString(), eq("appointment.confirmed"), captor.capture());

                NotificationSuccessEvent successEvent = captor.getValue();
                assertThat(successEvent.getAppointmentId()).isEqualTo(55L);
                assertThat(successEvent.getNotificationId()).isNotNull();
                assertThat(successEvent.getNotificationId()).isPositive();
        }
}
