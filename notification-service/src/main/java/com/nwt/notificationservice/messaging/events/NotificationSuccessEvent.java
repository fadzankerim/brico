package com.nwt.notificationservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSuccessEvent {

    private Long appointmentId;
    private Long notificationId;
}
