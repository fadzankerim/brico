package com.nwt.salonservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HairdresserResponse {

    private Long id;
    private Long userId;
    private Long salonId;
    private String salonName;
    private String bio;
    private String specialties;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<WorkingHoursResponse> workingHours;
    private List<ServiceResponse> services;
}
