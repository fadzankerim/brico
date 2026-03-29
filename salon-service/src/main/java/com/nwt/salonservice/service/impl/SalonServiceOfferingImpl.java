package com.nwt.salonservice.service.impl;

import com.nwt.salonservice.dto.request.ServiceRequest;
import com.nwt.salonservice.dto.response.ServiceResponse;
import com.nwt.salonservice.exception.ResourceNotFoundException;
import com.nwt.salonservice.model.Salon;
import com.nwt.salonservice.model.SalonService;
import com.nwt.salonservice.repository.SalonRepository;
import com.nwt.salonservice.repository.SalonServiceRepository;
import com.nwt.salonservice.service.SalonServiceOffering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalonServiceOfferingImpl implements SalonServiceOffering {

    private final SalonServiceRepository salonServiceRepository;
    private final SalonRepository salonRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> findBySalonId(Long salonId) {
        return salonServiceRepository.findBySalonIdAndIsActiveTrue(salonId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse findById(Long id) {
        SalonService service = salonServiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        return toResponse(service);
    }

    @Override
    @Transactional
    public ServiceResponse create(ServiceRequest request) {
        Salon salon = salonRepository.findById(request.getSalonId())
            .orElseThrow(() -> new ResourceNotFoundException("Salon", request.getSalonId()));

        SalonService service = SalonService.builder()
            .salon(salon)
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .durationMinutes(request.getDurationMinutes())
            .isActive(true)
            .build();

        service = salonServiceRepository.save(service);
        return toResponse(service);
    }

    @Override
    @Transactional
    public ServiceResponse update(Long id, ServiceRequest request) {
        SalonService service = salonServiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service", id));

        if (request.getSalonId() != null && !request.getSalonId().equals(service.getSalon().getId())) {
            Salon salon = salonRepository.findById(request.getSalonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salon", request.getSalonId()));
            service.setSalon(salon);
        }

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());

        service = salonServiceRepository.save(service);
        return toResponse(service);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SalonService service = salonServiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        service.setIsActive(false);
        salonServiceRepository.save(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> findAllByIds(List<Long> ids) {
        return salonServiceRepository.findAllByIdIn(ids).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private ServiceResponse toResponse(SalonService service) {
        return ServiceResponse.builder()
            .id(service.getId())
            .salonId(service.getSalon() != null ? service.getSalon().getId() : null)
            .salonName(service.getSalon() != null ? service.getSalon().getName() : null)
            .name(service.getName())
            .description(service.getDescription())
            .price(service.getPrice())
            .durationMinutes(service.getDurationMinutes())
            .isActive(service.getIsActive())
            .build();
    }
}
