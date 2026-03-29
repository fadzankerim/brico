package com.nwt.appointmentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private Long clientId;
    private Long salonId;
    private Long hairdresserId;
    private Long appointmentId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
