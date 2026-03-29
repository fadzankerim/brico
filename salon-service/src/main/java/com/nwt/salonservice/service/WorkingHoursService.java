package com.nwt.salonservice.service;

import com.nwt.salonservice.dto.request.WorkingHoursRequest;
import com.nwt.salonservice.dto.response.WorkingHoursResponse;

import java.util.List;

public interface WorkingHoursService {

    List<WorkingHoursResponse> findByHairdresserId(Long hairdresserId);

    WorkingHoursResponse create(WorkingHoursRequest request);

    WorkingHoursResponse update(Long id, WorkingHoursRequest request);

    void delete(Long id);
}
