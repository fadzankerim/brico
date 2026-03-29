package com.nwt.salonservice.service;

import com.nwt.salonservice.dto.request.SalonRequest;
import com.nwt.salonservice.dto.response.SalonResponse;
import com.nwt.salonservice.dto.response.SalonStatsResponse;
import com.nwt.salonservice.dto.response.SalonSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SalonService {

    Page<SalonSummaryResponse> searchSalons(String city, Double minRating, Pageable pageable);

    SalonResponse findById(Long id);

    SalonResponse create(SalonRequest request);

    SalonResponse update(Long id, SalonRequest request, Long requestingUserId, String role);

    void delete(Long id, Long requestingUserId, String role);

    SalonResponse verify(Long id);

    SalonStatsResponse getStats(Long id);

    List<SalonSummaryResponse> findByOwnerId(Long ownerId);
}
