package com.nwt.salonservice.service.impl;

import com.nwt.salonservice.dto.request.FavoriteRequest;
import com.nwt.salonservice.dto.response.FavoriteResponse;
import com.nwt.salonservice.dto.response.SalonSummaryResponse;
import com.nwt.salonservice.exception.BusinessException;
import com.nwt.salonservice.exception.ResourceNotFoundException;
import com.nwt.salonservice.model.Favorite;
import com.nwt.salonservice.model.Salon;
import com.nwt.salonservice.repository.FavoriteRepository;
import com.nwt.salonservice.repository.SalonRepository;
import com.nwt.salonservice.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final SalonRepository salonRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponse> findByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FavoriteResponse create(FavoriteRequest request) {
        if (favoriteRepository.existsByUserIdAndSalonId(request.getUserId(), request.getSalonId())) {
            throw new BusinessException("Salon is already in favorites", HttpStatus.CONFLICT, "ALREADY_EXISTS");
        }

        Salon salon = salonRepository.findById(request.getSalonId())
            .orElseThrow(() -> new ResourceNotFoundException("Salon", request.getSalonId()));

        Favorite favorite = Favorite.builder()
            .userId(request.getUserId())
            .salon(salon)
            .build();

        favorite = favoriteRepository.save(favorite);
        return toResponse(favorite);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!favoriteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Favorite", id);
        }
        favoriteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndSalonId(Long userId, Long salonId) {
        return favoriteRepository.existsByUserIdAndSalonId(userId, salonId);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndSalonId(Long userId, Long salonId) {
        Favorite favorite = favoriteRepository.findByUserIdAndSalonId(userId, salonId)
            .orElseThrow(() -> new ResourceNotFoundException("Favorite", salonId));
        favoriteRepository.delete(favorite);
    }

    private FavoriteResponse toResponse(Favorite favorite) {
        Salon salon = favorite.getSalon();
        SalonSummaryResponse salonResponse = null;
        if (salon != null) {
            salonResponse = SalonSummaryResponse.builder()
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
        return FavoriteResponse.builder()
            .id(favorite.getId())
            .userId(favorite.getUserId())
            .salonId(salon != null ? salon.getId() : null)
            .salon(salonResponse)
            .build();
    }
}
