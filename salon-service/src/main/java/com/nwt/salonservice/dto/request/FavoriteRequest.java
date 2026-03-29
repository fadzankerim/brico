package com.nwt.salonservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Salon ID is required")
    private Long salonId;
}
