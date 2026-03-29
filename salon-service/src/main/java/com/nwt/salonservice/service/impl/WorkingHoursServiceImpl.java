package com.nwt.salonservice.service.impl;

import com.nwt.salonservice.dto.request.WorkingHoursRequest;
import com.nwt.salonservice.dto.response.WorkingHoursResponse;
import com.nwt.salonservice.exception.ResourceNotFoundException;
import com.nwt.salonservice.model.Hairdresser;
import com.nwt.salonservice.model.WorkingHours;
import com.nwt.salonservice.repository.HairdresserRepository;
import com.nwt.salonservice.repository.WorkingHoursRepository;
import com.nwt.salonservice.service.WorkingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkingHoursServiceImpl implements WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;
    private final HairdresserRepository hairdresserRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WorkingHoursResponse> findByHairdresserId(Long hairdresserId) {
        return workingHoursRepository.findByHairdresserId(hairdresserId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WorkingHoursResponse create(WorkingHoursRequest request) {
        Hairdresser hairdresser = hairdresserRepository.findById(request.getHairdresserId())
            .orElseThrow(() -> new ResourceNotFoundException("Hairdresser", request.getHairdresserId()));

        WorkingHours workingHours = WorkingHours.builder()
            .hairdresser(hairdresser)
            .dayOfWeek(request.getDayOfWeek())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .build();

        workingHours = workingHoursRepository.save(workingHours);
        return toResponse(workingHours);
    }

    @Override
    @Transactional
    public WorkingHoursResponse update(Long id, WorkingHoursRequest request) {
        WorkingHours workingHours = workingHoursRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("WorkingHours", id));

        workingHours.setDayOfWeek(request.getDayOfWeek());
        workingHours.setStartTime(request.getStartTime());
        workingHours.setEndTime(request.getEndTime());

        workingHours = workingHoursRepository.save(workingHours);
        return toResponse(workingHours);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!workingHoursRepository.existsById(id)) {
            throw new ResourceNotFoundException("WorkingHours", id);
        }
        workingHoursRepository.deleteById(id);
    }

    private WorkingHoursResponse toResponse(WorkingHours wh) {
        return WorkingHoursResponse.builder()
            .id(wh.getId())
            .hairdresserId(wh.getHairdresser() != null ? wh.getHairdresser().getId() : null)
            .dayOfWeek(wh.getDayOfWeek())
            .startTime(wh.getStartTime())
            .endTime(wh.getEndTime())
            .build();
    }
}
