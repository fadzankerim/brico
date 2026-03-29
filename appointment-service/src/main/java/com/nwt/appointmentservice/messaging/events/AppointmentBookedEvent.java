package com.nwt.appointmentservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentBookedEvent {

    private Long appointmentId;
    private Long clientId;
    private String clientEmail;
    private String clientName;
    private Long hairdresserId;
    private String hairdresserName;
    private Long salonId;
    private Long serviceId;
    private String serviceName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private LocalDateTime eventTimestamp;
}
