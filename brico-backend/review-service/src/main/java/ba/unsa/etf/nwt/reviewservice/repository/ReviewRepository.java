package ba.unsa.etf.nwt.reviewservice.repository;

import ba.unsa.etf.nwt.reviewservice.model.Review;
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

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.salonId = :salonId")
    Double calculateAverageRatingBySalonId(@Param("salonId") Long salonId);

    long countBySalonId(Long salonId);
}
