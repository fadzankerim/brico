package com.nwt.notificationservice.dto.request;

import com.nwt.notificationservice.model.NotificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationStatusRequest {

    @NotNull(message = "status is required")
    private NotificationStatus status;
}
