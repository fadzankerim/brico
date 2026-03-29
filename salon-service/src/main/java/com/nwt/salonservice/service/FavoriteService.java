package com.nwt.salonservice.service;

import com.nwt.salonservice.dto.request.FavoriteRequest;
import com.nwt.salonservice.dto.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {

    List<FavoriteResponse> findByUserId(Long userId);

    FavoriteResponse create(FavoriteRequest request);

    void delete(Long id);

    boolean existsByUserIdAndSalonId(Long userId, Long salonId);

    void deleteByUserIdAndSalonId(Long userId, Long salonId);
}
