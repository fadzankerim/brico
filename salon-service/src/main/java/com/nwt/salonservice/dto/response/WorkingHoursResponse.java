package com.nwt.salonservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkingHoursResponse {

    private Long id;
    private Long hairdresserId;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
