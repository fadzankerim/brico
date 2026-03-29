package com.nwt.salonservice.service.impl;

import com.nwt.salonservice.dto.request.HairdresserRequest;
import com.nwt.salonservice.dto.response.HairdresserResponse;
import com.nwt.salonservice.dto.response.ServiceResponse;
import com.nwt.salonservice.dto.response.WorkingHoursResponse;
import com.nwt.salonservice.exception.ResourceNotFoundException;
import com.nwt.salonservice.model.Hairdresser;
import com.nwt.salonservice.model.Salon;
import com.nwt.salonservice.model.SalonService;
import com.nwt.salonservice.repository.HairdresserRepository;
import com.nwt.salonservice.repository.SalonRepository;
import com.nwt.salonservice.repository.SalonServiceRepository;
import com.nwt.salonservice.repository.WorkingHoursRepository;
import com.nwt.salonservice.service.HairdresserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HairdresserServiceImpl implements HairdresserService {

    private final HairdresserRepository hairdresserRepository;
    private final SalonRepository salonRepository;
    private final SalonServiceRepository salonServiceRepository;
    private final WorkingHoursRepository workingHoursRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HairdresserResponse> findBySalonId(Long salonId) {
        return hairdresserRepository.findBySalonId(salonId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HairdresserResponse findById(Long id) {
        Hairdresser hairdresser = hairdresserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Hairdresser", id));
        return toResponse(hairdresser);
    }

    @Override
    @Transactional
    public HairdresserResponse create(HairdresserRequest request) {
        Salon salon = salonRepository.findById(request.getSalonId())
            .orElseThrow(() -> new ResourceNotFoundException("Salon", request.getSalonId()));

        Hairdresser hairdresser = Hairdresser.builder()
            .userId(request.getUserId())
            .salon(salon)
            .bio(request.getBio())
            .specialties(request.getSpecialties())
            .isActive(true)
            .build();

        hairdresser = hairdresserRepository.save(hairdresser);
        return toResponse(hairdresser);
    }

    @Override
    @Transactional
    public HairdresserResponse update(Long id, HairdresserRequest request) {
        Hairdresser hairdresser = hairdresserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Hairdresser", id));

        if (request.getSalonId() != null && !request.getSalonId().equals(hairdresser.getSalon().getId())) {
            Salon salon = salonRepository.findById(request.getSalonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salon", request.getSalonId()));
            hairdresser.setSalon(salon);
        }

        if (request.getBio() != null) hairdresser.setBio(request.getBio());
        if (request.getSpecialties() != null) hairdresser.setSpecialties(request.getSpecialties());

        hairdresser = hairdresserRepository.save(hairdresser);
        return toResponse(hairdresser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Hairdresser hairdresser = hairdresserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Hairdresser", id));
        hairdresser.setIsActive(false);
        hairdresserRepository.save(hairdresser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkingHoursResponse> getWorkingHours(Long hairdresserId) {
        if (!hairdresserRepository.existsById(hairdresserId)) {
            throw new ResourceNotFoundException("Hairdresser", hairdresserId);
        }
        return workingHoursRepository.findByHairdresserId(hairdresserId).stream()
            .map(wh -> WorkingHoursResponse.builder()
                .id(wh.getId())
                .hairdresserId(hairdresserId)
                .dayOfWeek(wh.getDayOfWeek())
                .startTime(wh.getStartTime())
                .endTime(wh.getEndTime())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HairdresserResponse assignService(Long hairdresserId, Long serviceId) {
        Hairdresser hairdresser = hairdresserRepository.findById(hairdresserId)
            .orElseThrow(() -> new ResourceNotFoundException("Hairdresser", hairdresserId));
        SalonService service = salonServiceRepository.findById(serviceId)
            .orElseThrow(() -> new ResourceNotFoundException("Service", serviceId));

        if (!hairdresser.getServices().contains(service)) {
            hairdresser.getServices().add(service);
            hairdresser = hairdresserRepository.save(hairdresser);
        }
        return toResponse(hairdresser);
    }

    @Override
    @Transactional
    public HairdresserResponse removeService(Long hairdresserId, Long serviceId) {
        Hairdresser hairdresser = hairdresserRepository.findById(hairdresserId)
            .orElseThrow(() -> new ResourceNotFoundException("Hairdresser", hairdresserId));
        SalonService service = salonServiceRepository.findById(serviceId)
            .orElseThrow(() -> new ResourceNotFoundException("Service", serviceId));

        hairdresser.getServices().remove(service);
        hairdresser = hairdresserRepository.save(hairdresser);
        return toResponse(hairdresser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HairdresserResponse> findAllByIds(List<Long> ids) {
        return hairdresserRepository.findAllByIdIn(ids).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private HairdresserResponse toResponse(Hairdresser hairdresser) {
        HairdresserResponse response = HairdresserResponse.builder()
            .id(hairdresser.getId())
            .userId(hairdresser.getUserId())
            .salonId(hairdresser.getSalon() != null ? hairdresser.getSalon().getId() : null)
            .salonName(hairdresser.getSalon() != null ? hairdresser.getSalon().getName() : null)
            .bio(hairdresser.getBio())
            .specialties(hairdresser.getSpecialties())
            .isActive(hairdresser.getIsActive())
            .createdAt(hairdresser.getCreatedAt())
            .build();

        if (hairdresser.getWorkingHours() != null) {
            response.setWorkingHours(hairdresser.getWorkingHours().stream()
                .map(wh -> WorkingHoursResponse.builder()
                    .id(wh.getId())
                    .hairdresserId(hairdresser.getId())
                    .dayOfWeek(wh.getDayOfWeek())
                    .startTime(wh.getStartTime())
                    .endTime(wh.getEndTime())
                    .build())
                .collect(Collectors.toList()));
        }

        if (hairdresser.getServices() != null) {
            response.setServices(hairdresser.getServices().stream()
                .map(s -> ServiceResponse.builder()
                    .id(s.getId())
                    .salonId(hairdresser.getSalon() != null ? hairdresser.getSalon().getId() : null)
                    .name(s.getName())
                    .description(s.getDescription())
                    .price(s.getPrice())
                    .durationMinutes(s.getDurationMinutes())
                    .isActive(s.getIsActive())
                    .build())
                .collect(Collectors.toList()));
        }

        return response;
    }
}
