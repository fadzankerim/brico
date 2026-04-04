package ba.unsa.etf.nwt.salonservice.controller;

import ba.unsa.etf.nwt.salonservice.dto.*;
import ba.unsa.etf.nwt.salonservice.service.SalonMgmtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
@Tag(name = "Salons", description = "Upravljanje salonima")
public class SalonController {

    private final SalonMgmtService salonService;

    @GetMapping
    @Operation(summary = "Dohvati sve salone")
    public ResponseEntity<List<SalonResponse>> getAll() {
        return ResponseEntity.ok(salonService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dohvati salon po ID-u")
    public ResponseEntity<SalonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salonService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Dohvati salon po slug-u")
    public ResponseEntity<SalonResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(salonService.findBySlug(slug));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Dohvati salone po vlasniku")
    public ResponseEntity<List<SalonResponse>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(salonService.findByOwnerId(ownerId));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novi salon")
    public ResponseEntity<SalonResponse> create(@Valid @RequestBody SalonRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salonService.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Ažuriraj salon")
    public ResponseEntity<SalonResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody SalonRequest req) {
        return ResponseEntity.ok(salonService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obriši salon")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        salonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Hairdressers ───────────────────────────────────────────────────

    @GetMapping("/{salonId}/hairdressers")
    @Operation(summary = "Dohvati frizere salona")
    public ResponseEntity<List<HairdresserResponse>> getHairdressers(@PathVariable Long salonId) {
        return ResponseEntity.ok(salonService.findHairdressersBySalon(salonId));
    }

    @PostMapping("/{salonId}/hairdressers")
    @Operation(summary = "Dodaj frizera u salon")
    public ResponseEntity<HairdresserResponse> addHairdresser(@PathVariable Long salonId,
                                                               @Valid @RequestBody HairdresserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salonService.addHairdresser(salonId, req));
    }

    @PutMapping("/{salonId}/hairdressers/{hairdresserId}")
    @Operation(summary = "Ažuriraj frizera")
    public ResponseEntity<HairdresserResponse> updateHairdresser(@PathVariable Long salonId,
                                                                   @PathVariable Long hairdresserId,
                                                                   @Valid @RequestBody HairdresserRequest req) {
        return ResponseEntity.ok(salonService.updateHairdresser(salonId, hairdresserId, req));
    }

    @DeleteMapping("/{salonId}/hairdressers/{hairdresserId}")
    @Operation(summary = "Ukloni frizera iz salona")
    public ResponseEntity<Void> removeHairdresser(@PathVariable Long salonId,
                                                   @PathVariable Long hairdresserId) {
        salonService.removeHairdresser(salonId, hairdresserId);
        return ResponseEntity.noContent().build();
    }

    // ── Services ───────────────────────────────────────────────────────

    @GetMapping("/{salonId}/services")
    @Operation(summary = "Dohvati usluge salona")
    public ResponseEntity<List<ServiceResponse>> getServices(@PathVariable Long salonId) {
        return ResponseEntity.ok(salonService.findServicesBySalon(salonId));
    }

    @PostMapping("/{salonId}/services")
    @Operation(summary = "Dodaj uslugu salonu")
    public ResponseEntity<ServiceResponse> addService(@PathVariable Long salonId,
                                                      @Valid @RequestBody ServiceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salonService.addService(salonId, req));
    }

    @PutMapping("/{salonId}/services/{serviceId}")
    @Operation(summary = "Ažuriraj uslugu")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long salonId,
                                                         @PathVariable Long serviceId,
                                                         @Valid @RequestBody ServiceRequest req) {
        return ResponseEntity.ok(salonService.updateService(salonId, serviceId, req));
    }

    @DeleteMapping("/{salonId}/services/{serviceId}")
    @Operation(summary = "Obriši uslugu")
    public ResponseEntity<Void> deleteService(@PathVariable Long salonId,
                                              @PathVariable Long serviceId) {
        salonService.deleteService(salonId, serviceId);
        return ResponseEntity.noContent().build();
    }

    // ── Photos ─────────────────────────────────────────────────────────

    @GetMapping("/{salonId}/photos")
    @Operation(summary = "Dohvati fotografije salona")
    public ResponseEntity<List<PhotoResponse>> getPhotos(@PathVariable Long salonId) {
        return ResponseEntity.ok(salonService.findPhotosBySalon(salonId));
    }

    @PostMapping("/{salonId}/photos")
    @Operation(summary = "Dodaj fotografiju salonu")
    public ResponseEntity<PhotoResponse> addPhoto(@PathVariable Long salonId,
                                                  @Valid @RequestBody PhotoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salonService.addPhoto(salonId, req));
    }

    @PatchMapping("/{salonId}/photos/{photoId}/primary")
    @Operation(summary = "Postavi fotografiju kao naslovnu")
    public ResponseEntity<PhotoResponse> setPrimary(@PathVariable Long salonId,
                                                    @PathVariable Long photoId) {
        return ResponseEntity.ok(salonService.setPrimary(salonId, photoId));
    }

    @DeleteMapping("/{salonId}/photos/{photoId}")
    @Operation(summary = "Obriši fotografiju")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long salonId,
                                            @PathVariable Long photoId) {
        salonService.deletePhoto(salonId, photoId);
        return ResponseEntity.noContent().build();
    }
}
