package ba.unsa.etf.nwt.salonservice.repository;

import ba.unsa.etf.nwt.salonservice.model.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    List<WorkingHours> findBySalonId(Long salonId);

    Optional<WorkingHours> findBySalonIdAndDayOfWeek(Long salonId, Integer dayOfWeek);
}
