package ba.unsa.etf.nwt.bookingservice.service;

import ba.unsa.etf.nwt.bookingservice.client.SalonClient;
import ba.unsa.etf.nwt.bookingservice.client.UserClient;
import ba.unsa.etf.nwt.bookingservice.dto.*;
import ba.unsa.etf.nwt.bookingservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.bookingservice.messaging.AppointmentCreatedEvent;
import ba.unsa.etf.nwt.bookingservice.messaging.AppointmentEventPublisher;
import ba.unsa.etf.nwt.bookingservice.model.*;
import ba.unsa.etf.nwt.bookingservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final UserClient userClient;
    private final SalonClient salonClient;
    private final AppointmentEventPublisher eventPublisher;

    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    public List<AppointmentResponse> findBySalonId(Long salonId) {
        return appointmentRepository.findBySalonId(salonId).stream()
                .map(this::toResponse).toList();
    }

    public List<AppointmentResponse> findByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(this::toResponse).toList();
    }

    public List<AppointmentResponse> findByHairdresserId(Long hairdresserId) {
        return appointmentRepository.findByHairdresserId(hairdresserId).stream()
                .map(this::toResponse).toList();
    }

    // ── Paginacija ────────────────────────────────────────────────────

    public Page<AppointmentResponse> findAllPaged(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<AppointmentResponse> findBySalonIdPaged(Long salonId, Pageable pageable) {
        return appointmentRepository.findBySalonId(salonId, pageable).map(this::toResponse);
    }

    public Page<AppointmentResponse> findByClientIdPaged(Long clientId, Pageable pageable) {
        return appointmentRepository.findByClientId(clientId, pageable).map(this::toResponse);
    }

    // ── Custom query — vremenski raspon ──────────────────────────────

    public List<AppointmentResponse> findByHairdresserAndDateRange(
            Long hairdresserId, LocalDateTime from, LocalDateTime to) {
        return appointmentRepository.findActiveByHairdresserAndDateRange(hairdresserId, from, to)
                .stream().map(this::toResponse).toList();
    }

    public List<AppointmentResponse> findBySalonAndDateRange(
            Long salonId, LocalDateTime from, LocalDateTime to) {
        return appointmentRepository.findBySalonIdAndDateRange(salonId, from, to)
                .stream().map(this::toResponse).toList();
    }

    // ── Statistika ────────────────────────────────────────────────────

    public Map<String, Object> getSalonStats(Long salonId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("salonId", salonId);
        stats.put("total",     appointmentRepository.countBySalonId(salonId));
        stats.put("pending",   appointmentRepository.countBySalonIdAndStatus(salonId, AppointmentStatus.PENDING));
        stats.put("confirmed", appointmentRepository.countBySalonIdAndStatus(salonId, AppointmentStatus.CONFIRMED));
        stats.put("completed", appointmentRepository.countBySalonIdAndStatus(salonId, AppointmentStatus.COMPLETED));
        stats.put("cancelled", appointmentRepository.countBySalonIdAndStatus(salonId, AppointmentStatus.CANCELLED));
        stats.put("revenue",   appointmentRepository.sumRevenueBySalon(salonId));
        return stats;
    }

    public AppointmentResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    /**
     * Validacija za inter-service komunikaciju.
     * review-service poziva ovaj endpoint da provjeri status termina prije kreiranja recenzije.
     */
    public Map<String, Object> validate(Long id) {
        Appointment appt = getOrThrow(id);
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("id", appt.getId());
        result.put("status", appt.getStatus().name());
        result.put("salonId", appt.getSalonId());
        result.put("clientId", appt.getClientId());
        result.put("completed", appt.getStatus() == AppointmentStatus.COMPLETED);
        return result;
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest req) {
        // ── Inter-service validacija (sinhrona komunikacija putem Feign + Eureka) ──
        // 1. Provjeri da klijent postoji i aktivan je u user-service (samo ako je clientId poznat)
        if (req.getClientId() != null) {
            Map<String, Object> userInfo = userClient.validateUser(req.getClientId());
            if (Boolean.FALSE.equals(userInfo.get("active"))) {
                throw new IllegalStateException("Klijent sa ID=" + req.getClientId() + " nije aktivan");
            }
        }

        // 2. Provjeri da salon postoji i aktivan je u salon-service
        Map<String, Object> salonInfo = salonClient.validateSalon(req.getSalonId());
        if (Boolean.FALSE.equals(salonInfo.get("active"))) {
            throw new IllegalStateException("Salon sa ID=" + req.getSalonId() + " nije aktivan");
        }

        // 3. Provjeri da svaka tražena usluga postoji u salon-service
        for (AppointmentItemRequest item : req.getItems()) {
            if (item.getServiceId() != null) {
                Map<String, Object> svcInfo = salonClient.validateService(req.getSalonId(), item.getServiceId());
                if (Boolean.FALSE.equals(svcInfo.get("active"))) {
                    throw new IllegalStateException("Usluga sa ID=" + item.getServiceId() + " nije aktivna");
                }
            }
        }

        BigDecimal total = req.getItems().stream()
                .map(AppointmentItemRequest::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalMinutes = req.getItems().stream()
                .mapToInt(AppointmentItemRequest::getDurationMinutes).sum();

        Appointment appt = Appointment.builder()
                .clientId(req.getClientId())
                .clientName(req.getClientName())
                .clientPhone(req.getClientPhone())
                .hairdresserId(req.getHairdresserId())
                .hairdresserName(req.getHairdresserName())
                .salonId(req.getSalonId())
                .salonName(req.getSalonName())
                .salonAddress(req.getSalonAddress())
                .startTime(req.getStartTime())
                .endTime(req.getStartTime().plusMinutes(totalMinutes))
                .totalPrice(total)
                .notes(req.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();

        for (AppointmentItemRequest item : req.getItems()) {
            AppointmentItem ai = AppointmentItem.builder()
                    .serviceId(item.getServiceId())
                    .serviceName(item.getServiceName())
                    .price(item.getPrice())
                    .durationMinutes(item.getDurationMinutes())
                    .appointment(appt)
                    .build();
            appt.getItems().add(ai);
        }

        Appointment saved = appointmentRepository.save(appt);

        // ── Saga: objavi async event za rezervaciju slota u salon-serviceu ──
        eventPublisher.publishAppointmentCreated(new AppointmentCreatedEvent(
                saved.getId(),
                saved.getHairdresserId(),
                saved.getSalonId(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getClientId()
        ));

        return toResponse(saved);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appt = getOrThrow(id);
        appt.setStatus(status);
        return toResponse(appointmentRepository.save(appt));
    }

    // ── PATCH — pomjeranje termina ────────────────────────────────────

    @Transactional
    public AppointmentResponse reschedule(Long id, RescheduleRequest req) {
        Appointment appt = getOrThrow(id);
        if (appt.getStatus() == AppointmentStatus.COMPLETED ||
            appt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Završeni ili otkazani termini se ne mogu pomjeriti");
        }
        long durationMinutes = java.time.Duration.between(appt.getStartTime(), appt.getEndTime()).toMinutes();
        appt.setStartTime(req.getNewStartTime());
        appt.setEndTime(req.getNewStartTime().plusMinutes(durationMinutes));
        appt.setStatus(AppointmentStatus.PENDING); // reset na pending nakon pomjeranja
        if (req.getReason() != null) appt.setNotes(req.getReason());
        return toResponse(appointmentRepository.save(appt));
    }

    @Transactional
    public AppointmentResponse cancel(Long id, String reason) {
        Appointment appt = getOrThrow(id);
        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Završeni termini se ne mogu otkazati");
        }
        if (appt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Termin je već otkazan");
        }
        appt.setStatus(AppointmentStatus.CANCELLED);
        appt.setCancelReason(reason);
        appt.setCancelledAt(LocalDateTime.now());
        Appointment saved = appointmentRepository.save(appt);

        // Obavijesti klijenta da mu je termin otkazan
        eventPublisher.publishNotification(
                saved.getClientId(),
                "APPOINTMENT_CANCELLED",
                "Termin otkazan",
                "Vaš termin u salonu " + saved.getSalonName() + " je otkazan." +
                (reason != null && !reason.isBlank() ? " Razlog: " + reason : ""),
                saved.getId()
        );

        return toResponse(saved);
    }

    private Appointment getOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Termin sa ID=" + id + " nije pronađen"));
    }

    private AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse r = modelMapper.map(a, AppointmentResponse.class);
        r.setItems(a.getItems().stream()
                .map(i -> modelMapper.map(i, AppointmentItemResponse.class)).toList());
        return r;
    }
}
