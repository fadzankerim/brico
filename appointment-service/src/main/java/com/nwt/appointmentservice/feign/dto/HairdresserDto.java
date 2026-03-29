package com.nwt.appointmentservice.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HairdresserDto {

    private Long id;
    private Long salonId;
    private String bio;
    private List<String> specialties;
    private Boolean isActive;
    private String fullName;
}
