package com.nwt.salonservice.controller;

import com.nwt.salonservice.dto.request.SalonRequest;
import com.nwt.salonservice.dto.response.SalonResponse;
import com.nwt.salonservice.dto.response.SalonStatsResponse;
import com.nwt.salonservice.dto.response.SalonSummaryResponse;
import com.nwt.salonservice.exception.BusinessException;
import com.nwt.salonservice.service.SalonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    public ResponseEntity<Page<SalonSummaryResponse>> searchSalons(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) Double minRating,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) String sort,
        @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(salonService.searchSalons(city, minRating, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalonResponse> getSalonById(@PathVariable Long id) {
        return ResponseEntity.ok(salonService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SalonResponse> createSalon(
        @Valid @RequestBody SalonRequest request,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can create salons", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(salonService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalonResponse> updateSalon(
        @PathVariable Long id,
        @Valid @RequestBody SalonRequest request,
        @RequestHeader(value = "X-User-Id", required = false) Long userId,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (userId == null) {
            throw new BusinessException("User identification required", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        return ResponseEntity.ok(salonService.update(id, request, userId, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalon(
        @PathVariable Long id,
        @RequestHeader(value = "X-User-Id", required = false) Long userId,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (userId == null) {
            throw new BusinessException("User identification required", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        salonService.delete(id, userId, role);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<SalonResponse> verifySalon(
        @PathVariable Long id,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"ADMIN".equals(role)) {
            throw new BusinessException("Only ADMIN can verify salons", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        return ResponseEntity.ok(salonService.verify(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<SalonSummaryResponse>> getSalonsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(salonService.findByOwnerId(ownerId));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<SalonStatsResponse> getSalonStats(@PathVariable Long id) {
        return ResponseEntity.ok(salonService.getStats(id));
    }
}
