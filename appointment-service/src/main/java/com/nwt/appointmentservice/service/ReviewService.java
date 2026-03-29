package com.nwt.appointmentservice.service;

import com.nwt.appointmentservice.dto.request.CreateReviewRequest;
import com.nwt.appointmentservice.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(Long clientId, CreateReviewRequest request);

    ReviewResponse getReviewById(Long id);

    Page<ReviewResponse> getReviewsBySalonId(Long salonId, Pageable pageable);

    Page<ReviewResponse> getReviewsByHairdresserId(Long hairdresserId, Pageable pageable);

    void deleteReview(Long id);
}
