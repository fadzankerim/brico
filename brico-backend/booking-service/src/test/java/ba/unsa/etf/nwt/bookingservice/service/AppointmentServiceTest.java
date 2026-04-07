package ba.unsa.etf.nwt.bookingservice.service;

import ba.unsa.etf.nwt.bookingservice.dto.*;
import ba.unsa.etf.nwt.bookingservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.bookingservice.model.*;
import ba.unsa.etf.nwt.bookingservice.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock AppointmentRepository appointmentRepository;
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

    @Test
    void create_calculatesTotalAndEndTime() {
        when(appointmentRepository.save(any())).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(5L);
            return a;
        });

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

        AppointmentResponse resp = appointmentService.create(req);
        assertThat(resp.getTotalPrice()).isEqualByComparingTo("20.00");
        assertThat(resp.getStatus()).isEqualTo(AppointmentStatus.PENDING);
    }

    @Test
    void cancel_completedAppointment_throwsIllegalState() {
        Appointment a = sampleAppointment();
        a.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> appointmentService.cancel(1L))
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
}
