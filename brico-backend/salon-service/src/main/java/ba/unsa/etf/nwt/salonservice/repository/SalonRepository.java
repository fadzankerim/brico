package ba.unsa.etf.nwt.salonservice.repository;

import ba.unsa.etf.nwt.salonservice.model.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {

    Optional<Salon> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Salon> findByCity(String city);

    List<Salon> findByIsActive(Boolean isActive);

    List<Salon> findByOwnerId(Long ownerId);

    List<Salon> findByCityAndIsActive(String city, Boolean isActive);
}
