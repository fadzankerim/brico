package com.nwt.salonservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonStatsResponse {

    private Long totalServices;
    private Long totalHairdressers;
    private Double avgRating;
}
