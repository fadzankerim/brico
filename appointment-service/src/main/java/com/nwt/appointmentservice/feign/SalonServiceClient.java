package com.nwt.appointmentservice.feign;

import com.nwt.appointmentservice.feign.dto.HairdresserDto;
import com.nwt.appointmentservice.feign.dto.ServiceDto;
import com.nwt.appointmentservice.feign.dto.WorkingHoursDto;
import com.nwt.appointmentservice.feign.fallback.SalonServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "salon-service", fallback = SalonServiceFallback.class)
public interface SalonServiceClient {

    @GetMapping("/api/hairdressers/{id}")
    HairdresserDto getHairdresserById(@PathVariable("id") Long id);

    @GetMapping("/api/hairdressers/batch")
    List<HairdresserDto> getHairdressersByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("/api/services/{id}")
    ServiceDto getServiceById(@PathVariable("id") Long id);

    @GetMapping("/api/working-hours/hairdresser/{id}")
    List<WorkingHoursDto> getWorkingHours(@PathVariable("id") Long hairdresserId);
}
