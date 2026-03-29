package com.nwt.notificationservice.dto.request;

import com.nwt.notificationservice.model.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "appointmentId is required")
    private Long appointmentId;

    @NotNull(message = "type is required")
    private NotificationType type;

    private LocalDateTime scheduledAt;

    private String message;
}
