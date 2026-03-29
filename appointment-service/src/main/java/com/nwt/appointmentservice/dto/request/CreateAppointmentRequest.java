package com.nwt.appointmentservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAppointmentRequest {

    @NotNull(message = "Hairdresser ID is required")
    private Long hairdresserId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Salon ID is required")
    private Long salonId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    private String note;
}
