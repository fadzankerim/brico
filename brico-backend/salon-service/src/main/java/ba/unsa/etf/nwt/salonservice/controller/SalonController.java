package ba.unsa.etf.nwt.salonservice.controller;

import ba.unsa.etf.nwt.salonservice.dto.*;
import ba.unsa.etf.nwt.salonservice.repository.BookedSlotRepository;
import ba.unsa.etf.nwt.salonservice.service.SalonMgmtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
@Tag(name = "Salons", description = "Upravljanje salonima")
public class SalonController {

    private final SalonMgmtService      salonService;
    private final BookedSlotRepository  bookedSlotRepository;

    @GetMapping
    @Operation(summary = "Dohvati sve salone")
    public ResponseEntity<List<SalonResponse>> getAll() {
        return ResponseEntity.ok(salonService.findAll());
    }

    // ── Paginacija + sortiranje ────────────────────────────────────────

    @GetMapping("/paged")
    @Operation(summary = "Dohvati salone sa paginacijom, filtriranjem i sortiranjem")
    public ResponseEntity<Page<SalonResponse>> getAllPaged(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String q,
            @Parameter(hidden = true)
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(salonService.search(q, pageable));
        }
        if (city != null || minRating != null) {
            List<SalonResponse> filtered = salonService.searchByCityAndRating(city, minRating);
            int start = (int) pageable.getOffset();
            int end   = Math.min(start + pageable.getPageSize(), filtered.size());
            List<SalonResponse> pageContent = start >= filtered.size() ? List.of() : filtered.subList(start, end);
            return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size()));
        }
        return ResponseEntity.ok(salonService.findAllPaged(pageable));
    }

    // ── Custom pretraga ────────────────────────────────────────────────

    @GetMapping("/search")
    @Operation(summary = "Pretraži salone po imenu ili gradu")
    public ResponseEntity<Page<SalonResponse>> search(
            @RequestParam String q,
            @Parameter(hidden = true)
            @PageableDefault(size = 12, sort = "avgRating", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(salonService.search(q, pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filtriraj salone po gradu i minimalnoj ocjeni")
    public ResponseEntity<List<SalonResponse>> filter(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minRating) {
        return ResponseEntity.ok(salonService.searchByCityAndRating(city, minRating));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dohvati salon po ID-u")
    public ResponseEntity<SalonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salonService.findById(id));
    }

    /**
     * Interni endpoint za inter-service validaciju.
     * Koristi ga booking-service da provjeri da li salon postoji i aktivan je.
     */
    @GetMapping("/{id}/validate")
    @Operation(summary = "[Internal] Validacija salona za inter-service komunikaciju")
    public ResponseEntity<Map<String, Object>> validateSalon(@PathVariable Long id) {
        return ResponseEntity.ok(salonService.validateSalon(id));
    }

    /**
     * Interni endpoint za inter-service validaciju usluge.
     * Koristi ga booking-service da provjeri da usluga postoji i pripada traženom salonu.
     */
    @GetMapping("/{salonId}/services/{serviceId}/validate")
    @Operation(summary = "[Internal] Validacija usluge za inter-service komunikaciju")
    public ResponseEntity<Map<String, Object>> validateService(@PathVariable Long salonId,
                                                               @PathVariable Long serviceId) {
        return ResponseEntity.ok(salonService.validateService(salonId, serviceId));
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

    // ── Availability ──────────────────────────────────────────────────

    @GetMapping("/{salonId}/hairdressers/{hairdresserId}/availability")
    @Operation(summary = "Slobodni termini frizera za određeni datum i trajanje")
    public ResponseEntity<Map<String, Object>> getAvailability(
            @PathVariable Long salonId,
            @PathVariable Long hairdresserId,
            @RequestParam String date,
            @RequestParam(defaultValue = "30") int duration) {

        LocalDate day = LocalDate.parse(date);
        LocalDateTime dayStart = day.atTime(8, 0);
        LocalDateTime dayEnd   = day.atTime(20, 0);

        List<Map<String, Object>> slots = new ArrayList<>();
        LocalDateTime current = dayStart;

        while (!current.plusMinutes(duration).isAfter(dayEnd)) {
            LocalDateTime slotEnd = current.plusMinutes(duration);
            boolean conflict = bookedSlotRepository.existsConflict(
                    hairdresserId, current, slotEnd,
                    ba.unsa.etf.nwt.salonservice.model.BookedSlot.SlotStatus.RESERVED);

            Map<String, Object> slot = new LinkedHashMap<>();
            slot.put("time", current.toLocalTime().toString());
            slot.put("available", !conflict);
            slots.add(slot);
            current = current.plusMinutes(30);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date);
        result.put("hairdresserId", hairdresserId);
        result.put("totalDuration", duration);
        result.put("slots", slots);
        return ResponseEntity.ok(result);
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

    @PostMapping("/{salonId}/services/batch")
    @Operation(summary = "Dodaj više usluga odjednom (batch insert)")
    public ResponseEntity<List<ServiceResponse>> addServicesBatch(
            @PathVariable Long salonId,
            @Valid @RequestBody List<ServiceRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salonService.addServicesBatch(salonId, requests));
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
