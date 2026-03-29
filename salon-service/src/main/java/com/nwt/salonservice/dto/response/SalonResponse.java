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
public class SalonResponse {

    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private String city;
    private String address;
    private Double lat;
    private Double lng;
    private Double avgRating;
    private Boolean verified;
    private String phone;
    private LocalDateTime createdAt;
    private List<HairdresserResponse> hairdressers;
    private List<ServiceResponse> services;
}
