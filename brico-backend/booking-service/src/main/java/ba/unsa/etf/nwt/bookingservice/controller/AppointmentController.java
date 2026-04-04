package ba.unsa.etf.nwt.bookingservice.controller;

import ba.unsa.etf.nwt.bookingservice.dto.AppointmentRequest;
import ba.unsa.etf.nwt.bookingservice.dto.AppointmentResponse;
import ba.unsa.etf.nwt.bookingservice.model.AppointmentStatus;
import ba.unsa.etf.nwt.bookingservice.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Otkaži termin")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
        return ResponseEntity.ok(appointmentService.findById(id));
    }
}
