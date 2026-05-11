package ba.unsa.etf.nwt.salonservice.repository;

import ba.unsa.etf.nwt.salonservice.model.Hairdresser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HairdresserRepository extends JpaRepository<Hairdresser, Long> {

    List<Hairdresser> findBySalonId(Long salonId);

    List<Hairdresser> findBySalonIdAndIsActive(Long salonId, Boolean isActive);

    long countBySalonId(Long salonId);
    long countBySalonIdAndIsActive(Long salonId, Boolean isActive);

    List<Hairdresser> findByUserId(Long userId);
}
