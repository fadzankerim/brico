package com.nwt.appointmentservice.service.impl;

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
import com.nwt.appointmentservice.messaging.events.AppointmentBookedEvent;
import com.nwt.appointmentservice.model.Appointment;
import com.nwt.appointmentservice.model.AppointmentStatus;
import com.nwt.appointmentservice.repository.AppointmentRepository;
import com.nwt.appointmentservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private static final int SLOT_DURATION_MINUTES = 30;
    private static final int MIN_CANCELLATION_HOURS = 2;

    // Valid status transitions: key -> allowed next statuses
    private static final java.util.Map<AppointmentStatus, Set<AppointmentStatus>> VALID_TRANSITIONS =
            java.util.Map.of(
                AppointmentStatus.PENDING,   Set.of(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED),
                AppointmentStatus.CONFIRMED, Set.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED),
                AppointmentStatus.COMPLETED, Set.of(),
                AppointmentStatus.CANCELLED, Set.of()
            );

    private final AppointmentRepository appointmentRepository;
    private final UserServiceClient userServiceClient;
    private final SalonServiceClient salonServiceClient;
    private final AppointmentEventPublisher eventPublisher;
    private final SystemEventsClient systemEventsClient;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(Long clientId, CreateAppointmentRequest request) {
        log.info("Creating appointment for clientId={}, hairdresserId={}", clientId, request.getHairdresserId());

        // 1. Validate client
        UserDto client = userServiceClient.getUserById(clientId);
        if (client == null || client.getId() == null) {
            throw new ResourceNotFoundException("Client", clientId);
        }

        // 2. Validate hairdresser
        HairdresserDto hairdresser = salonServiceClient.getHairdresserById(request.getHairdresserId());
        if (hairdresser == null || hairdresser.getId() == null) {
            throw new ResourceNotFoundException("Hairdresser", request.getHairdresserId());
        }
        if (Boolean.FALSE.equals(hairdresser.getIsActive())) {
            throw new BusinessException("HAIRDRESSER_INACTIVE",
                    "Hairdresser " + request.getHairdresserId() + " is not active");
        }

        // 3. Validate service and get price/duration
        ServiceDto service = salonServiceClient.getServiceById(request.getServiceId());
        if (service == null || service.getId() == null) {
            throw new ResourceNotFoundException("Service", request.getServiceId());
        }
        if (Boolean.FALSE.equals(service.getIsActive())) {
            throw new BusinessException("SERVICE_INACTIVE",
                    "Service " + request.getServiceId() + " is not active");
        }

        // 4. Check working hours
        List<WorkingHoursDto> workingHours = salonServiceClient.getWorkingHours(request.getHairdresserId());
        LocalDateTime startTime = request.getStartTime();
        validateWithinWorkingHours(workingHours, startTime);

        // Calculate end time from service duration
        int durationMinutes = service.getDurationMinutes() != null ? service.getDurationMinutes() : 60;
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        // 5. Check for conflicts
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                request.getHairdresserId(), startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new BusinessException("TIME_SLOT_UNAVAILABLE",
                    "The requested time slot is not available for hairdresser " + request.getHairdresserId());
        }

        // 6. Create appointment
        Appointment appointment = Appointment.builder()
                .clientId(clientId)
                .hairdresserId(request.getHairdresserId())
                .serviceId(request.getServiceId())
                .salonId(request.getSalonId())
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .price(service.getPrice())
                .note(request.getNote())
                .build();

        appointment = appointmentRepository.save(appointment);
        log.info("Appointment created with id={}", appointment.getId());

        // 7. Publish event to RabbitMQ
        AppointmentBookedEvent event = AppointmentBookedEvent.builder()
                .appointmentId(appointment.getId())
                .clientId(clientId)
                .clientEmail(client.getEmail())
                .clientName(client.getFullName())
                .hairdresserId(request.getHairdresserId())
                .hairdresserName(hairdresser.getFullName())
                .salonId(request.getSalonId())
                .serviceId(request.getServiceId())
                .serviceName(service.getName())
                .startTime(startTime)
                .endTime(endTime)
                .price(service.getPrice())
                .eventTimestamp(LocalDateTime.now())
                .build();

        eventPublisher.publishAppointmentBooked(event);

        // Log system event
        systemEventsClient.logEvent("appointment-service", clientId,
                "APPOINTMENT_CREATED", "Appointment", "SUCCESS");

        return mapToResponse(appointment, client.getFullName(),
                hairdresser.getFullName(), service.getName());
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = findAppointmentById(id);
        return enrichAndMapToResponse(appointment);
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsByClientId(Long clientId, Pageable pageable) {
        return appointmentRepository.findByClientId(clientId, pageable)
                .map(this::enrichAndMapToResponse);
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsByHairdresserId(Long hairdresserId, Pageable pageable) {
        return appointmentRepository.findByHairdresserId(hairdresserId, pageable)
                .map(this::enrichAndMapToResponse);
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsBySalonId(Long salonId, LocalDate date, Pageable pageable) {
        Page<Appointment> page;
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            page = appointmentRepository.findBySalonIdAndDateRange(salonId, startOfDay, endOfDay, pageable);
        } else {
            page = appointmentRepository.findBySalonId(salonId, pageable);
        }
        return page.map(this::enrichAndMapToResponse);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long id, UpdateAppointmentStatusRequest request) {
        Appointment appointment = findAppointmentById(id);
        AppointmentStatus currentStatus = appointment.getStatus();
        AppointmentStatus newStatus = request.getStatus();

        Set<AppointmentStatus> allowed = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new BusinessException("INVALID_STATUS_TRANSITION",
                    String.format("Cannot transition from %s to %s", currentStatus, newStatus));
        }

        appointment.setStatus(newStatus);
        appointment = appointmentRepository.save(appointment);

        systemEventsClient.logEvent("appointment-service", appointment.getClientId(),
                "APPOINTMENT_STATUS_UPDATED", "Appointment", "SUCCESS");

        return enrichAndMapToResponse(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = findAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("ALREADY_CANCELLED", "Appointment is already cancelled");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("CANNOT_CANCEL_COMPLETED", "Cannot cancel a completed appointment");
        }

        // Enforce minimum 2 hours before start time
        LocalDateTime minCancellationTime = appointment.getStartTime().minusHours(MIN_CANCELLATION_HOURS);
        if (LocalDateTime.now().isAfter(minCancellationTime)) {
            throw new BusinessException("CANCELLATION_WINDOW_EXPIRED",
                    "Cannot cancel appointment less than " + MIN_CANCELLATION_HOURS + " hours before start time",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        systemEventsClient.logEvent("appointment-service", appointment.getClientId(),
                "APPOINTMENT_CANCELLED", "Appointment", "SUCCESS");

        log.info("Appointment {} cancelled", id);
    }

    @Override
    public List<LocalTime> getAvailableSlots(Long hairdresserId, LocalDate date) {
        // Fetch working hours for that day
        List<WorkingHoursDto> allWorkingHours = salonServiceClient.getWorkingHours(hairdresserId);
        WorkingHoursDto workingHoursForDay = allWorkingHours.stream()
                .filter(wh -> wh.getDayOfWeek() != null && wh.getDayOfWeek() == date.getDayOfWeek())
                .findFirst()
                .orElse(null);

        if (workingHoursForDay == null) {
            log.info("Hairdresser {} does not work on {}", hairdresserId, date.getDayOfWeek());
            return List.of();
        }

        // Generate 30-minute slots from start to end working time
        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime current = workingHoursForDay.getStartTime();
        LocalTime end = workingHoursForDay.getEndTime();

        while (current.plusMinutes(SLOT_DURATION_MINUTES).compareTo(end) <= 0) {
            allSlots.add(current);
            current = current.plusMinutes(SLOT_DURATION_MINUTES);
        }

        // Get already booked appointments for that day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Appointment> booked = appointmentRepository.findActiveByHairdresserAndDate(
                hairdresserId, startOfDay, endOfDay);

        // Remove slots that overlap with existing bookings
        List<LocalTime> available = new ArrayList<>();
        for (LocalTime slot : allSlots) {
            LocalDateTime slotStart = date.atTime(slot);
            LocalDateTime slotEnd = slotStart.plusMinutes(SLOT_DURATION_MINUTES);

            boolean hasConflict = booked.stream().anyMatch(a ->
                    a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(slotStart)
            );

            if (!hasConflict) {
                available.add(slot);
            }
        }

        return available;
    }

    // ---- Private helpers ----

    private Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    private void validateWithinWorkingHours(List<WorkingHoursDto> workingHours, LocalDateTime startTime) {
        if (workingHours == null || workingHours.isEmpty()) {
            throw new BusinessException("NO_WORKING_HOURS", "No working hours defined for this hairdresser");
        }

        WorkingHoursDto todayHours = workingHours.stream()
                .filter(wh -> wh.getDayOfWeek() != null && wh.getDayOfWeek() == startTime.getDayOfWeek())
                .findFirst()
                .orElseThrow(() -> new BusinessException("NOT_WORKING_DAY",
                        "The hairdresser does not work on " + startTime.getDayOfWeek()));

        LocalTime requestedTime = startTime.toLocalTime();
        if (requestedTime.isBefore(todayHours.getStartTime()) ||
                requestedTime.isAfter(todayHours.getEndTime())) {
            throw new BusinessException("OUTSIDE_WORKING_HOURS",
                    String.format("Requested time %s is outside working hours %s - %s",
                            requestedTime, todayHours.getStartTime(), todayHours.getEndTime()));
        }
    }

    private AppointmentResponse enrichAndMapToResponse(Appointment appointment) {
        String clientName = null;
        String hairdresserName = null;
        String serviceName = null;

        try {
            UserDto client = userServiceClient.getUserById(appointment.getClientId());
            if (client != null) clientName = client.getFullName();
        } catch (Exception e) {
            log.warn("Could not enrich client name for appointment {}: {}", appointment.getId(), e.getMessage());
        }

        try {
            HairdresserDto hairdresser = salonServiceClient.getHairdresserById(appointment.getHairdresserId());
            if (hairdresser != null) hairdresserName = hairdresser.getFullName();
        } catch (Exception e) {
            log.warn("Could not enrich hairdresser name for appointment {}: {}", appointment.getId(), e.getMessage());
        }

        try {
            ServiceDto service = salonServiceClient.getServiceById(appointment.getServiceId());
            if (service != null) serviceName = service.getName();
        } catch (Exception e) {
            log.warn("Could not enrich service name for appointment {}: {}", appointment.getId(), e.getMessage());
        }

        return mapToResponse(appointment, clientName, hairdresserName, serviceName);
    }

    private AppointmentResponse mapToResponse(Appointment appointment,
                                               String clientName,
                                               String hairdresserName,
                                               String serviceName) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .clientId(appointment.getClientId())
                .clientName(clientName)
                .hairdresserId(appointment.getHairdresserId())
                .hairdresserName(hairdresserName)
                .serviceId(appointment.getServiceId())
                .serviceName(serviceName)
                .salonId(appointment.getSalonId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .price(appointment.getPrice())
                .note(appointment.getNote())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
