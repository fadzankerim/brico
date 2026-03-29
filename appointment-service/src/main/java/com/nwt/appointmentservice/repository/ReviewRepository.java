package com.nwt.appointmentservice.repository;

import com.nwt.appointmentservice.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findBySalonId(Long salonId, Pageable pageable);

    Page<Review> findByHairdresserId(Long hairdresserId, Pageable pageable);

    Optional<Review> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);
}
