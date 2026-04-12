package ba.unsa.etf.nwt.reviewservice.repository;

import ba.unsa.etf.nwt.reviewservice.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findBySalonId(Long salonId);

    List<Review> findByClientId(Long clientId);

    List<Review> findByHairdresserId(Long hairdresserId);

    boolean existsByClientIdAndAppointmentId(Long clientId, Long appointmentId);

    // Paginacija + sortiranje
    Page<Review> findBySalonId(Long salonId, Pageable pageable);

    Page<Review> findByClientId(Long clientId, Pageable pageable);

    // Custom JPQL — prosječna ocjena
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.salonId = :salonId")
    Double calculateAverageRatingBySalonId(@Param("salonId") Long salonId);

    long countBySalonId(Long salonId);

    // Distribucija ocjena po salonima (1-5)
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.salonId = :salonId GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> countBySalonIdGroupByRating(@Param("salonId") Long salonId);

    // Recenzije sa odgovorom vlasnika
    @Query("SELECT r FROM Review r WHERE r.salonId = :salonId AND r.ownerReply IS NOT NULL")
    List<Review> findBySalonIdWithOwnerReply(@Param("salonId") Long salonId);
}
