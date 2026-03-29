package com.nwt.notificationservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookedEvent {

    private Long appointmentId;
    private Long clientId;
    private Long hairdresserId;
    private Long serviceId;
    private Long salonId;
    private String startTime;
    private String clientEmail;
    private String clientName;
}
