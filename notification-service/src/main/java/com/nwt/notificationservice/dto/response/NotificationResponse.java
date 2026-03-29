package com.nwt.notificationservice.dto.response;

import com.nwt.notificationservice.model.Notification;
import com.nwt.notificationservice.model.NotificationStatus;
import com.nwt.notificationservice.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long userId;
    private Long appointmentId;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private String message;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .appointmentId(notification.getAppointmentId())
                .type(notification.getType())
                .status(notification.getStatus())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
