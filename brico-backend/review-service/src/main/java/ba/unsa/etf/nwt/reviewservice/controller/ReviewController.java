package ba.unsa.etf.nwt.reviewservice.controller;

import ba.unsa.etf.nwt.reviewservice.dto.*;
import ba.unsa.etf.nwt.reviewservice.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reviews & Favorites", description = "Recenzije i omiljeni saloni")
public class ReviewController {

    private final ReviewService reviewService;

    // ── Reviews ────────────────────────────────────────────────────────

    @GetMapping("/reviews/{id}")
    @Operation(summary = "Dohvati recenziju po ID-u")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @GetMapping("/salons/{salonId}/reviews")
    @Operation(summary = "Dohvati recenzije salona")
    public ResponseEntity<List<ReviewResponse>> getBySalon(@PathVariable Long salonId) {
        return ResponseEntity.ok(reviewService.findBySalonId(salonId));
    }

    // ── Paginacija ────────────────────────────────────────────────────

    @GetMapping("/salons/{salonId}/reviews/paged")
    @Operation(summary = "Dohvati recenzije salona sa paginacijom i sortiranjem")
    public ResponseEntity<Page<ReviewResponse>> getBySalonPaged(
            @PathVariable Long salonId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.findBySalonIdPaged(salonId, pageable));
    }

    @GetMapping("/clients/{clientId}/reviews/paged")
    @Operation(summary = "Dohvati recenzije klijenta sa paginacijom")
    public ResponseEntity<Page<ReviewResponse>> getByClientPaged(
            @PathVariable Long clientId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.findByClientIdPaged(clientId, pageable));
    }

    // ── Statistika ────────────────────────────────────────────────────

    @GetMapping("/salons/{salonId}/rating")
    @Operation(summary = "Dohvati prosječnu ocjenu salona")
    public ResponseEntity<Map<String, Object>> getSalonRating(@PathVariable Long salonId) {
        return ResponseEntity.ok(reviewService.getSalonRating(salonId));
    }

    @GetMapping("/salons/{salonId}/reviews/stats")
    @Operation(summary = "Statistika recenzija salona — distribucija ocjena, prosjek, ukupan broj")
    public ResponseEntity<Map<String, Object>> getSalonReviewStats(@PathVariable Long salonId) {
        return ResponseEntity.ok(reviewService.getSalonReviewStats(salonId));
    }

    @GetMapping("/clients/{clientId}/reviews")
    @Operation(summary = "Dohvati recenzije klijenta")
    public ResponseEntity<List<ReviewResponse>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(reviewService.findByClientId(clientId));
    }

    @PostMapping("/reviews")
    @Operation(summary = "Kreiraj recenziju")
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(req));
    }

    // ── PATCH — odgovor vlasnika salona ───────────────────────────────

    @PatchMapping("/reviews/{id}/reply")
    @Operation(summary = "Vlasnik dodaje odgovor na recenziju")
    public ResponseEntity<ReviewResponse> addOwnerReply(@PathVariable Long id,
                                                         @Valid @RequestBody OwnerReplyRequest req) {
        return ResponseEntity.ok(reviewService.addOwnerReply(id, req));
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Obriši recenziju")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Favorites ──────────────────────────────────────────────────────

    @GetMapping("/users/{userId}/favorites")
    @Operation(summary = "Dohvati omiljene salone korisnika")
    public ResponseEntity<List<FavoriteResponse>> getFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.findFavoritesByUser(userId));
    }

    // Shortcut — koristi X-User-Id header koji gateway ubacuje iz JWT tokena
    @GetMapping("/favorites")
    @Operation(summary = "Dohvati omiljene salone prijavljenog korisnika (iz tokena)")
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(reviewService.findFavoritesByUser(userId));
    }

    @PostMapping("/favorites")
    @Operation(summary = "Dodaj salon u omiljene")
    public ResponseEntity<FavoriteResponse> addFavorite(@Valid @RequestBody FavoriteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addFavorite(req));
    }

    @DeleteMapping("/users/{userId}/favorites/{salonId}")
    @Operation(summary = "Ukloni salon iz omiljenih")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long userId,
                                                @PathVariable Long salonId) {
        reviewService.removeFavorite(userId, salonId);
        return ResponseEntity.noContent().build();
    }

    // Shortcut za brisanje iz favorita po salonId, userId iz tokena
    @DeleteMapping("/favorites/{salonId}")
    @Operation(summary = "Ukloni salon iz omiljenih (userId iz tokena)")
    public ResponseEntity<Void> removeMyFavorite(
            @PathVariable Long salonId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId != null) reviewService.removeFavorite(userId, salonId);
        return ResponseEntity.noContent().build();
    }
}
