package com.nwt.appointmentservice.service;

import com.nwt.appointmentservice.dto.request.CreateAppointmentRequest;
import com.nwt.appointmentservice.dto.request.UpdateAppointmentStatusRequest;
import com.nwt.appointmentservice.dto.response.AppointmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse createAppointment(Long clientId, CreateAppointmentRequest request);

    AppointmentResponse getAppointmentById(Long id);

    Page<AppointmentResponse> getAppointmentsByClientId(Long clientId, Pageable pageable);

    Page<AppointmentResponse> getAppointmentsByHairdresserId(Long hairdresserId, Pageable pageable);

    Page<AppointmentResponse> getAppointmentsBySalonId(Long salonId, LocalDate date, Pageable pageable);

    AppointmentResponse updateAppointmentStatus(Long id, UpdateAppointmentStatusRequest request);

    void cancelAppointment(Long id);

    List<LocalTime> getAvailableSlots(Long hairdresserId, LocalDate date);
}
