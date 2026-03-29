package com.nwt.salonservice.service.impl;

import com.nwt.salonservice.dto.request.SalonRequest;
import com.nwt.salonservice.dto.response.*;
import com.nwt.salonservice.exception.BusinessException;
import com.nwt.salonservice.exception.ResourceNotFoundException;
import com.nwt.salonservice.model.Salon;
import com.nwt.salonservice.repository.SalonRepository;
import com.nwt.salonservice.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalonServiceImpl implements SalonService {

    private final SalonRepository salonRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<SalonSummaryResponse> searchSalons(String city, Double minRating, Pageable pageable) {
        return salonRepository.searchSalons(city, minRating, pageable)
            .map(this::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SalonResponse findById(Long id) {
        Salon salon = salonRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salon", id));
        return toFullResponse(salon);
    }

    @Override
    @Transactional
    public SalonResponse create(SalonRequest request) {
        Salon salon = Salon.builder()
            .ownerId(request.getOwnerId())
            .name(request.getName())
            .description(request.getDescription())
            .city(request.getCity())
            .address(request.getAddress())
            .lat(request.getLat())
            .lng(request.getLng())
            .phone(request.getPhone())
            .avgRating(0.0)
            .verified(false)
            .build();
        salon = salonRepository.save(salon);
        return toFullResponse(salon);
    }

    @Override
    @Transactional
    public SalonResponse update(Long id, SalonRequest request, Long requestingUserId, String role) {
        Salon salon = salonRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salon", id));

        if (!"ADMIN".equals(role) && !salon.getOwnerId().equals(requestingUserId)) {
            throw new BusinessException("You are not authorized to update this salon", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        salon.setName(request.getName());
        salon.setDescription(request.getDescription());
        salon.setCity(request.getCity());
        salon.setAddress(request.getAddress());
        salon.setLat(request.getLat());
        salon.setLng(request.getLng());
        salon.setPhone(request.getPhone());

        salon = salonRepository.save(salon);
        return toFullResponse(salon);
    }

    @Override
    @Transactional
    public void delete(Long id, Long requestingUserId, String role) {
        Salon salon = salonRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salon", id));

        if (!"ADMIN".equals(role) && !salon.getOwnerId().equals(requestingUserId)) {
            throw new BusinessException("You are not authorized to delete this salon", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        salonRepository.delete(salon);
    }

    @Override
    @Transactional
    public SalonResponse verify(Long id) {
        Salon salon = salonRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salon", id));
        salon.setVerified(true);
        salon = salonRepository.save(salon);
        return toFullResponse(salon);
    }

    @Override
    @Transactional(readOnly = true)
    public SalonStatsResponse getStats(Long id) {
        Salon salon = salonRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salon", id));

        Long totalServices = salonRepository.countActiveServicesBySalonId(id);
        Long totalHairdressers = salonRepository.countActiveHairdressersBySalonId(id);

        return SalonStatsResponse.builder()
            .totalServices(totalServices)
            .totalHairdressers(totalHairdressers)
            .avgRating(salon.getAvgRating())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalonSummaryResponse> findByOwnerId(Long ownerId) {
        return salonRepository.findByOwnerId(ownerId).stream()
            .map(this::toSummaryResponse)
            .collect(Collectors.toList());
    }

    private SalonSummaryResponse toSummaryResponse(Salon salon) {
        return SalonSummaryResponse.builder()
            .id(salon.getId())
            .ownerId(salon.getOwnerId())
            .name(salon.getName())
            .description(salon.getDescription())
            .city(salon.getCity())
            .address(salon.getAddress())
            .lat(salon.getLat())
            .lng(salon.getLng())
            .avgRating(salon.getAvgRating())
            .verified(salon.getVerified())
            .phone(salon.getPhone())
            .createdAt(salon.getCreatedAt())
            .build();
    }

    private SalonResponse toFullResponse(Salon salon) {
        SalonResponse response = SalonResponse.builder()
            .id(salon.getId())
            .ownerId(salon.getOwnerId())
            .name(salon.getName())
            .description(salon.getDescription())
            .city(salon.getCity())
            .address(salon.getAddress())
            .lat(salon.getLat())
            .lng(salon.getLng())
            .avgRating(salon.getAvgRating())
            .verified(salon.getVerified())
            .phone(salon.getPhone())
            .createdAt(salon.getCreatedAt())
            .build();

        if (salon.getHairdressers() != null) {
            response.setHairdressers(salon.getHairdressers().stream()
                .map(h -> HairdresserResponse.builder()
                    .id(h.getId())
                    .userId(h.getUserId())
                    .salonId(salon.getId())
                    .salonName(salon.getName())
                    .bio(h.getBio())
                    .specialties(h.getSpecialties())
                    .isActive(h.getIsActive())
                    .createdAt(h.getCreatedAt())
                    .build())
                .collect(Collectors.toList()));
        }

        if (salon.getServices() != null) {
            response.setServices(salon.getServices().stream()
                .map(s -> ServiceResponse.builder()
                    .id(s.getId())
                    .salonId(salon.getId())
                    .salonName(salon.getName())
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
