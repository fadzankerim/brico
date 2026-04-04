package ba.unsa.etf.nwt.salonservice.repository;

import ba.unsa.etf.nwt.salonservice.model.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {

    List<SalonService> findBySalonId(Long salonId);

    List<SalonService> findBySalonIdAndIsActive(Long salonId, Boolean isActive);
}
