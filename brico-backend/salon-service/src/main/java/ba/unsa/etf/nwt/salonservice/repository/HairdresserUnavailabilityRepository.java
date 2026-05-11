package ba.unsa.etf.nwt.salonservice.repository;

import ba.unsa.etf.nwt.salonservice.model.HairdresserUnavailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HairdresserUnavailabilityRepository extends JpaRepository<HairdresserUnavailability, Long> {

    List<HairdresserUnavailability> findByHairdresserIdOrderByStartTimeAsc(Long hairdresserId);

    List<HairdresserUnavailability> findBySalonIdOrderByStartTimeAsc(Long salonId);

    @Query("""
        SELECT COUNT(u) > 0 FROM HairdresserUnavailability u
        WHERE u.hairdresserId = :hairdresserId
          AND u.startTime < :endTime
          AND u.endTime   > :startTime
    """)
    boolean existsUnavailability(@Param("hairdresserId") Long hairdresserId,
                                 @Param("startTime")     LocalDateTime startTime,
                                 @Param("endTime")       LocalDateTime endTime);
}
