package com.nwt.salonservice.controller;

import com.nwt.salonservice.dto.request.ServiceRequest;
import com.nwt.salonservice.dto.response.ServiceResponse;
import com.nwt.salonservice.exception.BusinessException;
import com.nwt.salonservice.service.SalonServiceOffering;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final SalonServiceOffering salonServiceOffering;

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<ServiceResponse>> getServicesBySalon(@PathVariable Long salonId) {
        return ResponseEntity.ok(salonServiceOffering.findBySalonId(salonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(salonServiceOffering.findById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(
        @Valid @RequestBody ServiceRequest request,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can create services", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(salonServiceOffering.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(
        @PathVariable Long id,
        @Valid @RequestBody ServiceRequest request,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can update services", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        return ResponseEntity.ok(salonServiceOffering.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(
        @PathVariable Long id,
        @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        if (!"SALON_OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("Only SALON_OWNER or ADMIN can delete services", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
        salonServiceOffering.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/batch")
    public ResponseEntity<List<ServiceResponse>> getServicesByIds(@RequestParam String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
            .map(String::trim)
            .map(Long::parseLong)
            .collect(Collectors.toList());
        return ResponseEntity.ok(salonServiceOffering.findAllByIds(idList));
    }
}
