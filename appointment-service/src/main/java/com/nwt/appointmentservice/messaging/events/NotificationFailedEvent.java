package com.nwt.appointmentservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationFailedEvent {

    private Long appointmentId;
    private Long clientId;
    private String reason;
    private LocalDateTime eventTimestamp;
}
