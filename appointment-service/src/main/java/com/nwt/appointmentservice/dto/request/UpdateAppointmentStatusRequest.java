package com.nwt.appointmentservice.dto.request;

import com.nwt.appointmentservice.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAppointmentStatusRequest {

    @NotNull(message = "Status is required")
    private AppointmentStatus status;
}
