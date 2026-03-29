package com.nwt.notificationservice.controller;

import com.nwt.notificationservice.dto.request.CreateNotificationRequest;
import com.nwt.notificationservice.dto.request.UpdateNotificationStatusRequest;
import com.nwt.notificationservice.dto.response.NotificationResponse;
import com.nwt.notificationservice.service.NotificationService;
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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /api/notifications/user/{userId}?page=0&size=20
     * Returns paginated notifications for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        log.info("GET /api/notifications/user/{}", userId);
        Page<NotificationResponse> notifications =
                notificationService.getNotificationsByUserId(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/{id}
     * Returns a single notification by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        log.info("GET /api/notifications/{}", id);
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    /**
     * POST /api/notifications
     * Creates a manual notification.
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {

        log.info("POST /api/notifications userId={}, appointmentId={}",
                request.getUserId(), request.getAppointmentId());
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/notifications/{id}/status
     * Updates the status of a notification.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<NotificationResponse> updateNotificationStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNotificationStatusRequest request) {

        log.info("PUT /api/notifications/{}/status -> {}", id, request.getStatus());
        return ResponseEntity.ok(notificationService.updateNotificationStatus(id, request));
    }
}
