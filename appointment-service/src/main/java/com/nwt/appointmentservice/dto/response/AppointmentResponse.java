package com.nwt.appointmentservice.dto.response;

import com.nwt.appointmentservice.model.AppointmentStatus;
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
public class AppointmentResponse {

    private Long id;
    private Long clientId;
    private String clientName;
    private Long hairdresserId;
    private String hairdresserName;
    private Long serviceId;
    private String serviceName;
    private Long salonId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private BigDecimal price;
    private String note;
    private LocalDateTime createdAt;
}
