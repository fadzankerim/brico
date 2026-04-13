package ba.unsa.etf.nwt.bookingservice.controller;

import ba.unsa.etf.nwt.bookingservice.dto.AppointmentRequest;
import ba.unsa.etf.nwt.bookingservice.dto.AppointmentResponse;
import ba.unsa.etf.nwt.bookingservice.dto.RescheduleRequest;
import ba.unsa.etf.nwt.bookingservice.model.AppointmentStatus;
import ba.unsa.etf.nwt.bookingservice.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Upravljanje terminima")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @Operation(summary = "Dohvati sve termine")
    public ResponseEntity<List<AppointmentResponse>> getAll(
            @RequestParam(required = false) Long salonId,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long hairdresserId) {
        if (salonId       != null) return ResponseEntity.ok(appointmentService.findBySalonId(salonId));
        if (clientId      != null) return ResponseEntity.ok(appointmentService.findByClientId(clientId));
        if (hairdresserId != null) return ResponseEntity.ok(appointmentService.findByHairdresserId(hairdresserId));
        return ResponseEntity.ok(appointmentService.findAll());
    }

    // ── Paginacija ────────────────────────────────────────────────────

    @GetMapping("/paged")
    @Operation(summary = "Dohvati termine sa paginacijom i sortiranjem")
    public ResponseEntity<Page<AppointmentResponse>> getAllPaged(
            @RequestParam(required = false) Long salonId,
            @RequestParam(required = false) Long clientId,
            @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        if (salonId  != null) return ResponseEntity.ok(appointmentService.findBySalonIdPaged(salonId, pageable));
        if (clientId != null) return ResponseEntity.ok(appointmentService.findByClientIdPaged(clientId, pageable));
        return ResponseEntity.ok(appointmentService.findAllPaged(pageable));
    }

    // ── Vremenski raspon ──────────────────────────────────────────────

    @GetMapping("/range")
    @Operation(summary = "Dohvati termine salona u vremenskom rasponu")
    public ResponseEntity<List<AppointmentResponse>> getByDateRange(
            @RequestParam Long salonId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(appointmentService.findBySalonAndDateRange(salonId, from, to));
    }

    // ── Statistika ────────────────────────────────────────────────────

    @GetMapping("/stats/salon/{salonId}")
    @Operation(summary = "Statistika termina salona (ukupno, po statusu, prihodi)")
    public ResponseEntity<Map<String, Object>> getSalonStats(@PathVariable Long salonId) {
        return ResponseEntity.ok(appointmentService.getSalonStats(salonId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dohvati termin po ID-u")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novi termin")
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(req));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Promijeni status termina")
    public ResponseEntity<AppointmentResponse> updateStatus(@PathVariable Long id,
                                                            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    // ── PATCH — pomjeranje termina ─────────────────────────────────────

    @PatchMapping("/{id}/reschedule")
    @Operation(summary = "Pomjeri termin na novi datum/sat")
    public ResponseEntity<AppointmentResponse> reschedule(@PathVariable Long id,
                                                          @Valid @RequestBody RescheduleRequest req) {
        return ResponseEntity.ok(appointmentService.reschedule(id, req));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Otkaži termin")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id,
                                                      @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(appointmentService.cancel(id, reason));
    }
}
