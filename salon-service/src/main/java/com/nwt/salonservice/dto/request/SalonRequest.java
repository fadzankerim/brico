package com.nwt.salonservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonRequest {

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotBlank(message = "Salon name is required")
    private String name;

    private String description;

    @NotBlank(message = "City is required")
    private String city;

    private String address;

    private Double lat;

    private Double lng;

    private String phone;
}
