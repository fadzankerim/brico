package com.nwt.salonservice.controller;

import com.nwt.salonservice.dto.request.HairdresserRequest;
import com.nwt.salonservice.dto.response.HairdresserResponse;
import com.nwt.salonservice.dto.response.WorkingHoursResponse;
import com.nwt.salonservice.exception.BusinessException;
import com.nwt.salonservice.service.HairdresserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hairdressers")
@RequiredArgsConstructor
public class HairdresserController {

    private final HairdresserService hairdresserService;

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<HairdresserResponse>> getHairdressersBySalon(@PathVariable Long salonId) {
        return ResponseEntity.ok(hairdresserService.findBySalonId(salonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HairdresserResponse> getHairdresserById(@PathVariable Long id) {
        return ResponseEntity.ok(hairdresserService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HairdresserResponse> createHairdresser(
        @Valid @RequestBody HairdresserRequest request,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can create hairdressers", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(hairdresserService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HairdresserResponse> updateHairdresser(
        @PathVariable Long id,
        @Valid @RequestBody HairdresserRequest request,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can update hairdressers", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        return ResponseEntity.ok(hairdresserService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHairdresser(
        @PathVariable Long id,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can delete hairdressers", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        hairdresserService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/working-hours")
    public ResponseEntity<List<WorkingHoursResponse>> getWorkingHours(@PathVariable Long id) {
        return ResponseEntity.ok(hairdresserService.getWorkingHours(id));
    }

    @PostMapping("/{id}/services/{serviceId}")
    public ResponseEntity<HairdresserResponse> assignService(
        @PathVariable Long id,
        @PathVariable Long serviceId,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can assign services", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        return ResponseEntity.ok(hairdresserService.assignService(id, serviceId));
    }

    @DeleteMapping("/{id}/services/{serviceId}")
    public ResponseEntity<HairdresserResponse> removeService(
        @PathVariable Long id,
        @PathVariable Long serviceId,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can remove services", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        return ResponseEntity.ok(hairdresserService.removeService(id, serviceId));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<HairdresserResponse>> getHairdressersByIds(
        @RequestParam String ids
    ) {
        List<Long> idList = Arrays.stream(ids.split(","))
            .map(String::trim)
            .map(Long::parseLong)
            .collect(Collectors.toList());
        return ResponseEntity.ok(hairdresserService.findAllByIds(idList));
    }
}
