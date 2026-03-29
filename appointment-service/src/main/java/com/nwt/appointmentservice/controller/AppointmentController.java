package com.nwt.appointmentservice.controller;

import com.nwt.appointmentservice.dto.request.CreateAppointmentRequest;
import com.nwt.appointmentservice.dto.request.UpdateAppointmentStatusRequest;
import com.nwt.appointmentservice.dto.response.AppointmentResponse;
import com.nwt.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * POST /api/appointments
     * Creates a new appointment asynchronously via RabbitMQ saga.
     * Returns 202 ACCEPTED - the booking is initiated; confirmation arrives via notification.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long clientId) {

        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "UNAUTHORIZED", "message", "User identification required"));
        }

        log.info("POST /api/appointments clientId={}", clientId);
        AppointmentResponse appointment = appointmentService.createAppointment(clientId, request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "message", "Booking initiated. You will be notified once your appointment is confirmed.",
                "appointmentId", appointment.getId(),
                "status", appointment.getStatus().name(),
                "startTime", appointment.getStartTime().toString(),
                "endTime", appointment.getEndTime().toString()
        ));
    }

    /**
     * GET /api/appointments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        log.info("GET /api/appointments/{}", id);
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    /**
     * GET /api/appointments/client/{clientId}?page=0&size=10
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<AppointmentResponse>> getAppointmentsByClient(
            @PathVariable Long clientId,
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable) {

        log.info("GET /api/appointments/client/{}", clientId);
        return ResponseEntity.ok(appointmentService.getAppointmentsByClientId(clientId, pageable));
    }

    /**
     * GET /api/appointments/hairdresser/{hairdresserId}?page=0&size=10
     */
    @GetMapping("/hairdresser/{hairdresserId}")
    public ResponseEntity<Page<AppointmentResponse>> getAppointmentsByHairdresser(
            @PathVariable Long hairdresserId,
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable) {

        log.info("GET /api/appointments/hairdresser/{}", hairdresserId);
        return ResponseEntity.ok(appointmentService.getAppointmentsByHairdresserId(hairdresserId, pageable));
    }

    /**
     * GET /api/appointments/salon/{salonId}?date=2026-03-29&page=0&size=10
     */
    @GetMapping("/salon/{salonId}")
    public ResponseEntity<Page<AppointmentResponse>> getAppointmentsBySalon(
            @PathVariable Long salonId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable) {

        log.info("GET /api/appointments/salon/{} date={}", salonId, date);
        return ResponseEntity.ok(appointmentService.getAppointmentsBySalonId(salonId, date, pageable));
    }

    /**
     * PATCH /api/appointments/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {

        log.info("PATCH /api/appointments/{}/status -> {}", id, request.getStatus());
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, request));
    }

    /**
     * DELETE /api/appointments/{id}
     * Cancels the appointment.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        log.info("DELETE /api/appointments/{} userId={}", id, userId);
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/appointments/available-slots?hairdresserId=1&date=2026-03-29
     * Returns list of available time slots for a hairdresser on a given date.
     */
    @GetMapping("/available-slots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam Long hairdresserId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("GET /api/appointments/available-slots hairdresserId={} date={}", hairdresserId, date);
        return ResponseEntity.ok(appointmentService.getAvailableSlots(hairdresserId, date));
    }
}
