package ba.unsa.etf.nwt.bookingservice.service;

import ba.unsa.etf.nwt.bookingservice.client.SalonClient;
import ba.unsa.etf.nwt.bookingservice.client.UserClient;
import ba.unsa.etf.nwt.bookingservice.messaging.AppointmentEventPublisher;
import ba.unsa.etf.nwt.bookingservice.dto.*;
import ba.unsa.etf.nwt.bookingservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.bookingservice.exception.ServiceUnavailableException;
import ba.unsa.etf.nwt.bookingservice.model.*;
import ba.unsa.etf.nwt.bookingservice.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock AppointmentRepository appointmentRepository;
    @Mock UserClient userClient;
    @Mock SalonClient salonClient;
    @Mock AppointmentEventPublisher eventPublisher;
    @InjectMocks AppointmentService appointmentService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setup() throws Exception {
        var field = AppointmentService.class.getDeclaredField("modelMapper");
        field.setAccessible(true);
        field.set(appointmentService, modelMapper);
    }

    private Appointment sampleAppointment() {
        AppointmentItem item = AppointmentItem.builder()
                .id(1L).serviceId(1L).serviceName("Šišanje")
                .price(new BigDecimal("15.00")).durationMinutes(30).build();

        Appointment a = Appointment.builder()
                .id(1L).clientId(1L).clientName("Ana Anić")
                .hairdresserId(1L).hairdresserName("Marko M.")
                .salonId(1L).salonName("Elite Cut")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusMinutes(30))
                .totalPrice(new BigDecimal("15.00"))
                .status(AppointmentStatus.PENDING)
                .build();
        item.setAppointment(a);
        a.getItems().add(item);
        return a;
    }

    @Test
    void findAll_returnsList() {
        when(appointmentRepository.findAll()).thenReturn(List.of(sampleAppointment()));
        List<AppointmentResponse> result = appointmentService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClientName()).isEqualTo("Ana Anić");
    }

    @Test
    void findById_existing_returnsAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(sampleAppointment()));
        AppointmentResponse resp = appointmentService.findById(1L);
        assertThat(resp.getSalonName()).isEqualTo("Elite Cut");
        assertThat(resp.getItems()).hasSize(1);
    }

    @Test
    void findById_unknown_throwsNotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> appointmentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private AppointmentRequest sampleRequest() {
        AppointmentItemRequest itemReq = new AppointmentItemRequest();
        itemReq.setServiceId(1L);
        itemReq.setServiceName("Šišanje");
        itemReq.setPrice(new BigDecimal("20.00"));
        itemReq.setDurationMinutes(45);

        AppointmentRequest req = new AppointmentRequest();
        req.setClientId(1L);
        req.setClientName("Ana");
        req.setHairdresserId(1L);
        req.setHairdresserName("Marko");
        req.setSalonId(1L);
        req.setSalonName("Elite Cut");
        req.setStartTime(LocalDateTime.now().plusDays(1));
        req.setItems(List.of(itemReq));
        return req;
    }

    @Test
    void create_calculatesTotalAndEndTime() {
        when(userClient.validateUser(1L)).thenReturn(Map.of("id", 1L, "active", true, "role", "CLIENT"));
        when(salonClient.validateSalon(1L)).thenReturn(Map.of("id", 1L, "active", true, "name", "Elite Cut"));
        when(salonClient.validateService(1L, 1L)).thenReturn(Map.of("id", 1L, "active", true, "name", "Šišanje"));
        when(appointmentRepository.save(any())).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(5L);
            return a;
        });

        AppointmentResponse resp = appointmentService.create(sampleRequest());
        assertThat(resp.getTotalPrice()).isEqualByComparingTo("20.00");
        assertThat(resp.getStatus()).isEqualTo(AppointmentStatus.PENDING);
    }

    // ── Novi testovi za Zadatak 5 — inter-service komunikacija ────────

    @Test
    void create_inactiveClient_throwsIllegalState() {
        when(userClient.validateUser(1L)).thenReturn(Map.of("id", 1L, "active", false, "role", "CLIENT"));

        assertThatThrownBy(() -> appointmentService.create(sampleRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nije aktivan");
    }

    @Test
    void create_inactiveSalon_throwsIllegalState() {
        when(userClient.validateUser(1L)).thenReturn(Map.of("id", 1L, "active", true, "role", "CLIENT"));
        when(salonClient.validateSalon(1L)).thenReturn(Map.of("id", 1L, "active", false, "name", "Elite Cut"));

        assertThatThrownBy(() -> appointmentService.create(sampleRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nije aktivan");
    }

    @Test
    void create_userServiceUnavailable_throwsServiceUnavailable() {
        when(userClient.validateUser(1L)).thenThrow(new ServiceUnavailableException("user-service nije dostupan"));

        assertThatThrownBy(() -> appointmentService.create(sampleRequest()))
                .isInstanceOf(ServiceUnavailableException.class);
    }

    @Test
    void cancel_completedAppointment_throwsIllegalState() {
        Appointment a = sampleAppointment();
        a.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> appointmentService.cancel(1L, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateStatus_changesStatus() {
        Appointment a = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));
        when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AppointmentResponse resp = appointmentService.updateStatus(1L, AppointmentStatus.CONFIRMED);
        assertThat(resp.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    }

    // ── Novi testovi za Zadatak 4 ─────────────────────────────────────

    @Test
    void findAllPaged_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(sampleAppointment()), pageable, 1);
        when(appointmentRepository.findAll(pageable)).thenReturn(page);

        Page<AppointmentResponse> result = appointmentService.findAllPaged(pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getSalonName()).isEqualTo("Elite Cut");
    }

    @Test
    void reschedule_pendingAppointment_updatesTime() {
        Appointment a = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));
        when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime newStart = LocalDateTime.now().plusDays(3);
        RescheduleRequest req = new RescheduleRequest();
        req.setNewStartTime(newStart);

        AppointmentResponse resp = appointmentService.reschedule(1L, req);
        assertThat(resp.getStartTime()).isEqualTo(newStart);
        assertThat(resp.getStatus()).isEqualTo(AppointmentStatus.PENDING);
    }

    @Test
    void reschedule_cancelledAppointment_throwsIllegalState() {
        Appointment a = sampleAppointment();
        a.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        RescheduleRequest req = new RescheduleRequest();
        req.setNewStartTime(LocalDateTime.now().plusDays(3));

        assertThatThrownBy(() -> appointmentService.reschedule(1L, req))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getSalonStats_returnsCompleteMap() {
        when(appointmentRepository.countBySalonId(1L)).thenReturn(10L);
        when(appointmentRepository.countBySalonIdAndStatus(1L, AppointmentStatus.PENDING)).thenReturn(3L);
        when(appointmentRepository.countBySalonIdAndStatus(1L, AppointmentStatus.CONFIRMED)).thenReturn(2L);
        when(appointmentRepository.countBySalonIdAndStatus(1L, AppointmentStatus.COMPLETED)).thenReturn(4L);
        when(appointmentRepository.countBySalonIdAndStatus(1L, AppointmentStatus.CANCELLED)).thenReturn(1L);
        when(appointmentRepository.sumRevenueBySalon(1L)).thenReturn(new BigDecimal("150.00"));

        Map<String, Object> stats = appointmentService.getSalonStats(1L);
        assertThat(stats.get("total")).isEqualTo(10L);
        assertThat(stats.get("completed")).isEqualTo(4L);
        assertThat(((BigDecimal) stats.get("revenue"))).isEqualByComparingTo(new BigDecimal("150.00"));
    }
}
