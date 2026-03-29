package com.nwt.salonservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {

    private Long id;
    private Long userId;
    private Long salonId;
    private SalonSummaryResponse salon;
}
