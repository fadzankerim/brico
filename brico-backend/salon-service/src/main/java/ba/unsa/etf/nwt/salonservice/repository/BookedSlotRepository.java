package ba.unsa.etf.nwt.salonservice.repository;

import ba.unsa.etf.nwt.salonservice.model.BookedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookedSlotRepository extends JpaRepository<BookedSlot, Long> {

    Optional<BookedSlot> findByAppointmentId(Long appointmentId);

    // Provjera da li frizer ima preklapajući RESERVED slot
    @Query("""
        SELECT COUNT(s) > 0 FROM BookedSlot s
        WHERE s.hairdresserId = :hairdresserId
          AND s.status = :status
          AND s.startTime < :endTime
          AND s.endTime   > :startTime
    """)
    boolean existsConflict(@Param("hairdresserId") Long hairdresserId,
                           @Param("startTime")     LocalDateTime startTime,
                           @Param("endTime")       LocalDateTime endTime,
                           @Param("status")        BookedSlot.SlotStatus status);
}
