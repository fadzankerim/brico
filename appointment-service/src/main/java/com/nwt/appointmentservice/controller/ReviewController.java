package com.nwt.appointmentservice.controller;

import com.nwt.appointmentservice.dto.request.CreateReviewRequest;
import com.nwt.appointmentservice.dto.response.ReviewResponse;
import com.nwt.appointmentservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /api/reviews
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long clientId) {

        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("POST /api/reviews clientId={} appointmentId={}", clientId, request.getAppointmentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(clientId, request));
    }

    /**
     * GET /api/reviews/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        log.info("GET /api/reviews/{}", id);
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    /**
     * GET /api/reviews/salon/{salonId}?page=0&size=10
     */
    @GetMapping("/salon/{salonId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsBySalon(
            @PathVariable Long salonId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        log.info("GET /api/reviews/salon/{}", salonId);
        return ResponseEntity.ok(reviewService.getReviewsBySalonId(salonId, pageable));
    }

    /**
     * GET /api/reviews/hairdresser/{hairdresserId}?page=0&size=10
     */
    @GetMapping("/hairdresser/{hairdresserId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByHairdresser(
            @PathVariable Long hairdresserId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        log.info("GET /api/reviews/hairdresser/{}", hairdresserId);
        return ResponseEntity.ok(reviewService.getReviewsByHairdresserId(hairdresserId, pageable));
    }

    /**
     * DELETE /api/reviews/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        log.info("DELETE /api/reviews/{}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
