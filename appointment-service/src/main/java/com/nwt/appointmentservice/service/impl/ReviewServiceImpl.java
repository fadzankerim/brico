package com.nwt.appointmentservice.service.impl;

import com.nwt.appointmentservice.dto.request.CreateReviewRequest;
import com.nwt.appointmentservice.dto.response.ReviewResponse;
import com.nwt.appointmentservice.exception.BusinessException;
import com.nwt.appointmentservice.exception.ResourceNotFoundException;
import com.nwt.appointmentservice.model.Appointment;
import com.nwt.appointmentservice.model.AppointmentStatus;
import com.nwt.appointmentservice.model.Review;
import com.nwt.appointmentservice.repository.AppointmentRepository;
import com.nwt.appointmentservice.repository.ReviewRepository;
import com.nwt.appointmentservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(Long clientId, CreateReviewRequest request) {
        log.info("Creating review for appointmentId={} by clientId={}", request.getAppointmentId(), clientId);

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", request.getAppointmentId()));

        if (!appointment.getClientId().equals(clientId)) {
            throw new BusinessException("FORBIDDEN",
                    "You can only review your own appointments", HttpStatus.FORBIDDEN);
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new BusinessException("APPOINTMENT_NOT_COMPLETED",
                    "You can only review completed appointments");
        }

        if (reviewRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new BusinessException("REVIEW_ALREADY_EXISTS",
                    "A review for appointment " + request.getAppointmentId() + " already exists");
        }

        Review review = Review.builder()
                .clientId(clientId)
                .salonId(request.getSalonId())
                .hairdresserId(request.getHairdresserId())
                .appointmentId(request.getAppointmentId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        log.info("Review created with id={}", review.getId());

        return mapToResponse(review);
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
        return mapToResponse(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsBySalonId(Long salonId, Pageable pageable) {
        return reviewRepository.findBySalonId(salonId, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<ReviewResponse> getReviewsByHairdresserId(Long hairdresserId, Pageable pageable) {
        return reviewRepository.findByHairdresserId(hairdresserId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", id);
        }
        reviewRepository.deleteById(id);
        log.info("Review {} deleted", id);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .clientId(review.getClientId())
                .salonId(review.getSalonId())
                .hairdresserId(review.getHairdresserId())
                .appointmentId(review.getAppointmentId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
