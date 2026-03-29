package com.nwt.salonservice.controller;

import com.nwt.salonservice.dto.request.WorkingHoursRequest;
import com.nwt.salonservice.dto.response.WorkingHoursResponse;
import com.nwt.salonservice.service.WorkingHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/working-hours")
@RequiredArgsConstructor
public class WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    @GetMapping("/hairdresser/{hairdresserId}")
    public ResponseEntity<List<WorkingHoursResponse>> getWorkingHoursByHairdresser(
        @PathVariable Long hairdresserId
    ) {
        return ResponseEntity.ok(workingHoursService.findByHairdresserId(hairdresserId));
    }

    @PostMapping
    public ResponseEntity<WorkingHoursResponse> createWorkingHours(
        @Valid @RequestBody WorkingHoursRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workingHoursService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkingHoursResponse> updateWorkingHours(
        @PathVariable Long id,
        @Valid @RequestBody WorkingHoursRequest request
    ) {
        return ResponseEntity.ok(workingHoursService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkingHours(@PathVariable Long id) {
        workingHoursService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
