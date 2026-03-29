package com.nwt.salonservice.service;

import com.nwt.salonservice.dto.request.HairdresserRequest;
import com.nwt.salonservice.dto.response.HairdresserResponse;
import com.nwt.salonservice.dto.response.WorkingHoursResponse;

import java.util.List;

public interface HairdresserService {

    List<HairdresserResponse> findBySalonId(Long salonId);

    HairdresserResponse findById(Long id);

    HairdresserResponse create(HairdresserRequest request);

    HairdresserResponse update(Long id, HairdresserRequest request);

    void delete(Long id);

    List<WorkingHoursResponse> getWorkingHours(Long hairdresserId);

    HairdresserResponse assignService(Long hairdresserId, Long serviceId);

    HairdresserResponse removeService(Long hairdresserId, Long serviceId);

    List<HairdresserResponse> findAllByIds(List<Long> ids);
}
