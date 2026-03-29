package com.nwt.appointmentservice.service;

import com.nwt.appointmentservice.dto.request.CreateAppointmentRequest;
import com.nwt.appointmentservice.dto.request.UpdateAppointmentStatusRequest;
import com.nwt.appointmentservice.dto.response.AppointmentResponse;
import com.nwt.appointmentservice.exception.BusinessException;
import com.nwt.appointmentservice.exception.ResourceNotFoundException;
import com.nwt.appointmentservice.feign.SalonServiceClient;
import com.nwt.appointmentservice.feign.UserServiceClient;
import com.nwt.appointmentservice.feign.dto.HairdresserDto;
import com.nwt.appointmentservice.feign.dto.ServiceDto;
import com.nwt.appointmentservice.feign.dto.UserDto;
import com.nwt.appointmentservice.feign.dto.WorkingHoursDto;
import com.nwt.appointmentservice.grpc.SystemEventsClient;
import com.nwt.appointmentservice.messaging.AppointmentEventPublisher;
import com.nwt.appointmentservice.model.Appointment;
import com.nwt.appointmentservice.model.AppointmentStatus;
import com.nwt.appointmentservice.repository.AppointmentRepository;
import com.nwt.appointmentservice.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private SalonServiceClient salonServiceClient;
    @Mock private AppointmentEventPublisher eventPublisher;
    @Mock private SystemEventsClient systemEventsClient;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment testAppointment;
    private UserDto testUser;
    private HairdresserDto testHairdresser;
    private ServiceDto testService;
    private WorkingHoursDto testWorkingHours;

    @BeforeEach
    void setUp() {
        LocalDateTime startTime = LocalDateTime.of(2026, 4, 6, 10, 0); // Monday
        LocalDateTime endTime = startTime.plusMinutes(60);

        testAppointment = Appointment.builder()
                .id(1L)
                .clientId(100L)
                .hairdresserId(200L)
                .serviceId(300L)
                .salonId(400L)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .price(new BigDecimal("45.00"))
                .build();

        testUser = new UserDto();
        testUser.setId(100L);
        testUser.setEmail("amela@test.com");
        testUser.setFullName("Amela Begović");

        testHairdresser = new HairdresserDto();
        testHairdresser.setId(200L);
        testHairdresser.setFullName("Maja Petrović");
        testHairdresser.setIsActive(true);

        testService = new ServiceDto();
        testService.setId(300L);
        testService.setName("Farbanje");
        testService.setPrice(new BigDecimal("45.00"));
        testService.setDurationMinutes(60);
        testService.setIsActive(true);

        testWorkingHours = new WorkingHoursDto();
        testWorkingHours.setDayOfWeek(DayOfWeek.MONDAY);
        testWorkingHours.setStartTime(LocalTime.of(9, 0));
        testWorkingHours.setEndTime(LocalTime.of(18, 0));
    }

    @Test
    void createAppointment_WithValidData_ReturnsAppointmentResponse() {
        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .hairdresserId(200L)
                .serviceId(300L)
                .salonId(400L)
                .startTime(testAppointment.getStartTime())
                .build();

        when(userServiceClient.getUserById(100L)).thenReturn(testUser);
        when(salonServiceClient.getHairdresserById(200L)).thenReturn(testHairdresser);
        when(salonServiceClient.getServiceById(300L)).thenReturn(testService);
        when(salonServiceClient.getWorkingHours(200L)).thenReturn(List.of(testWorkingHours));
        when(appointmentRepository.findConflictingAppointments(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        doNothing().when(eventPublisher).publishAppointmentBooked(any());
        doNothing().when(systemEventsClient).logEvent(any(), any(), any(), any(), any());

        AppointmentResponse result = appointmentService.createAppointment(100L, request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("45.00"));
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(eventPublisher, times(1)).publishAppointmentBooked(any());
    }

    @Test
    void createAppointment_WhenTimeSlotConflict_ThrowsBusinessException() {
        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .hairdresserId(200L)
                .serviceId(300L)
                .salonId(400L)
                .startTime(testAppointment.getStartTime())
                .build();

        when(userServiceClient.getUserById(100L)).thenReturn(testUser);
        when(salonServiceClient.getHairdresserById(200L)).thenReturn(testHairdresser);
        when(salonServiceClient.getServiceById(300L)).thenReturn(testService);
        when(salonServiceClient.getWorkingHours(200L)).thenReturn(List.of(testWorkingHours));
        when(appointmentRepository.findConflictingAppointments(any(), any(), any()))
                .thenReturn(List.of(testAppointment));

        assertThatThrownBy(() -> appointmentService.createAppointment(100L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void createAppointment_WhenHairdresserInactive_ThrowsBusinessException() {
        testHairdresser.setIsActive(false);
        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .hairdresserId(200L)
                .serviceId(300L)
                .salonId(400L)
                .startTime(testAppointment.getStartTime())
                .build();

        when(userServiceClient.getUserById(100L)).thenReturn(testUser);
        when(salonServiceClient.getHairdresserById(200L)).thenReturn(testHairdresser);

        assertThatThrownBy(() -> appointmentService.createAppointment(100L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not active");
    }

    @Test
    void getAppointmentById_WhenExists_ReturnsResponse() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(userServiceClient.getUserById(100L)).thenReturn(testUser);
        when(salonServiceClient.getHairdresserById(200L)).thenReturn(testHairdresser);
        when(salonServiceClient.getServiceById(300L)).thenReturn(testService);

        AppointmentResponse result = appointmentService.getAppointmentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClientName()).isEqualTo("Amela Begović");
    }

    @Test
    void getAppointmentById_WhenNotFound_ThrowsResourceNotFoundException() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getAppointmentById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateAppointmentStatus_ValidTransition_ReturnsUpdatedResponse() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        Appointment confirmed = Appointment.builder()
                .id(1L).clientId(100L).hairdresserId(200L).serviceId(300L).salonId(400L)
                .startTime(testAppointment.getStartTime()).endTime(testAppointment.getEndTime())
                .status(AppointmentStatus.CONFIRMED).price(testAppointment.getPrice()).build();
        when(appointmentRepository.save(any())).thenReturn(confirmed);
        when(userServiceClient.getUserById(100L)).thenReturn(testUser);
        when(salonServiceClient.getHairdresserById(200L)).thenReturn(testHairdresser);
        when(salonServiceClient.getServiceById(300L)).thenReturn(testService);
        doNothing().when(systemEventsClient).logEvent(any(), any(), any(), any(), any());

        UpdateAppointmentStatusRequest req = new UpdateAppointmentStatusRequest(AppointmentStatus.CONFIRMED);
        AppointmentResponse result = appointmentService.updateAppointmentStatus(1L, req);

        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    }

    @Test
    void updateAppointmentStatus_InvalidTransition_ThrowsBusinessException() {
        testAppointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        UpdateAppointmentStatusRequest req = new UpdateAppointmentStatusRequest(AppointmentStatus.CONFIRMED);

        assertThatThrownBy(() -> appointmentService.updateAppointmentStatus(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot transition");
    }

    @Test
    void getAvailableSlots_ReturnsFilteredSlots() {
        LocalDate date = LocalDate.of(2026, 4, 6); // April 6 2026 = Monday
        testWorkingHours.setDayOfWeek(DayOfWeek.MONDAY);
        when(salonServiceClient.getWorkingHours(200L)).thenReturn(List.of(testWorkingHours));
        when(appointmentRepository.findActiveByHairdresserAndDate(eq(200L), any(), any()))
                .thenReturn(Collections.emptyList());

        List<LocalTime> slots = appointmentService.getAvailableSlots(200L, date);

        assertThat(slots).isNotEmpty();
        assertThat(slots.get(0)).isEqualTo(LocalTime.of(9, 0));
    }
}
