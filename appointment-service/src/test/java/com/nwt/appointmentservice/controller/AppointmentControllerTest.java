package com.nwt.appointmentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nwt.appointmentservice.dto.request.CreateAppointmentRequest;
import com.nwt.appointmentservice.dto.request.UpdateAppointmentStatusRequest;
import com.nwt.appointmentservice.dto.response.AppointmentResponse;
import com.nwt.appointmentservice.exception.BusinessException;
import com.nwt.appointmentservice.exception.GlobalExceptionHandler;
import com.nwt.appointmentservice.exception.ResourceNotFoundException;
import com.nwt.appointmentservice.model.AppointmentStatus;
import com.nwt.appointmentservice.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private AppointmentResponse testResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        testResponse = AppointmentResponse.builder()
                .id(1L)
                .clientId(100L)
                .hairdresserId(200L)
                .serviceId(300L)
                .salonId(400L)
                .startTime(LocalDateTime.of(2026, 4, 7, 10, 0))
                .endTime(LocalDateTime.of(2026, 4, 7, 11, 0))
                .status(AppointmentStatus.PENDING)
                .price(new BigDecimal("45.00"))
                .build();
    }

    @Test
    void createAppointment_WithValidData_Returns202() throws Exception {
        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .hairdresserId(200L)
                .serviceId(300L)
                .salonId(400L)
                .startTime(LocalDateTime.of(2026, 4, 7, 10, 0))
                .build();

        when(appointmentService.createAppointment(eq(100L), any(CreateAppointmentRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "100"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createAppointment_WithoutUserId_Returns401() throws Exception {
        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .hairdresserId(200L)
                .serviceId(300L)
                .salonId(400L)
                .startTime(LocalDateTime.of(2026, 4, 7, 10, 0))
                .build();

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createAppointment_WithMissingField_Returns400() throws Exception {
        CreateAppointmentRequest invalid = CreateAppointmentRequest.builder()
                .hairdresserId(200L)
                .build();

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid))
                        .header("X-User-Id", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void getAppointmentById_WhenExists_Returns200() throws Exception {
        when(appointmentService.getAppointmentById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getAppointmentById_WhenNotFound_Returns404() throws Exception {
        when(appointmentService.getAppointmentById(999L))
                .thenThrow(new ResourceNotFoundException("Appointment", 999L));

        mockMvc.perform(get("/api/appointments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void updateAppointmentStatus_ValidTransition_Returns200() throws Exception {
        AppointmentResponse confirmed = AppointmentResponse.builder()
                .id(1L).status(AppointmentStatus.CONFIRMED).build();
        when(appointmentService.updateAppointmentStatus(eq(1L), any())).thenReturn(confirmed);

        UpdateAppointmentStatusRequest request = new UpdateAppointmentStatusRequest(AppointmentStatus.CONFIRMED);

        mockMvc.perform(patch("/api/appointments/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void updateAppointmentStatus_InvalidTransition_Returns400() throws Exception {
        when(appointmentService.updateAppointmentStatus(eq(1L), any()))
                .thenThrow(new BusinessException("INVALID_STATUS_TRANSITION",
                        "Cannot transition from COMPLETED to CONFIRMED"));

        UpdateAppointmentStatusRequest request = new UpdateAppointmentStatusRequest(AppointmentStatus.CONFIRMED);

        mockMvc.perform(patch("/api/appointments/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_STATUS_TRANSITION"));
    }

    @Test
    void cancelAppointment_Returns204() throws Exception {
        doNothing().when(appointmentService).cancelAppointment(1L);

        mockMvc.perform(delete("/api/appointments/1")
                        .header("X-User-Id", "100"))
                .andExpect(status().isNoContent());
    }
}
