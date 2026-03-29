package com.nwt.appointmentservice.feign.fallback;

import com.nwt.appointmentservice.feign.SalonServiceClient;
import com.nwt.appointmentservice.feign.dto.HairdresserDto;
import com.nwt.appointmentservice.feign.dto.ServiceDto;
import com.nwt.appointmentservice.feign.dto.WorkingHoursDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class SalonServiceFallback implements SalonServiceClient {

    @Override
    public HairdresserDto getHairdresserById(Long id) {
        log.warn("SalonService fallback triggered for getHairdresserById({})", id);
        return HairdresserDto.builder()
                .id(id)
                .isActive(false)
                .build();
    }

    @Override
    public List<HairdresserDto> getHairdressersByIds(List<Long> ids) {
        log.warn("SalonService fallback triggered for getHairdressersByIds({})", ids);
        return Collections.emptyList();
    }

    @Override
    public ServiceDto getServiceById(Long id) {
        log.warn("SalonService fallback triggered for getServiceById({})", id);
        return ServiceDto.builder()
                .id(id)
                .isActive(false)
                .build();
    }

    @Override
    public List<WorkingHoursDto> getWorkingHours(Long hairdresserId) {
        log.warn("SalonService fallback triggered for getWorkingHours({})", hairdresserId);
        return Collections.emptyList();
    }
}
