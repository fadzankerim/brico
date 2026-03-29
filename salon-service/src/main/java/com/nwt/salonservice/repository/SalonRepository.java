package com.nwt.salonservice.repository;

import com.nwt.salonservice.model.Salon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {

    @EntityGraph(attributePaths = {"hairdressers", "services"})
    Optional<Salon> findById(Long id);

    @Query("SELECT s FROM Salon s WHERE " +
           "(:city IS NULL OR LOWER(s.city) = LOWER(:city)) AND " +
           "(:minRating IS NULL OR s.avgRating >= :minRating) AND " +
           "s.verified = true")
    Page<Salon> searchSalons(
        @Param("city") String city,
        @Param("minRating") Double minRating,
        Pageable pageable
    );

    List<Salon> findByOwnerId(Long ownerId);

    @Query("SELECT COUNT(ss) FROM SalonService ss WHERE ss.salon.id = :salonId AND ss.isActive = true")
    Long countActiveServicesBySalonId(@Param("salonId") Long salonId);

    @Query("SELECT COUNT(h) FROM Hairdresser h WHERE h.salon.id = :salonId AND h.isActive = true")
    Long countActiveHairdressersBySalonId(@Param("salonId") Long salonId);
}
