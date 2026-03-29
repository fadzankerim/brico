package com.nwt.appointmentservice.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean isActive;
}
