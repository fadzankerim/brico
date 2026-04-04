package ba.unsa.etf.nwt.bookingservice.repository;

import ba.unsa.etf.nwt.bookingservice.model.Appointment;
import ba.unsa.etf.nwt.bookingservice.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findBySalonId(Long salonId);

    List<Appointment> findByHairdresserId(Long hairdresserId);

    List<Appointment> findByClientIdAndStatus(Long clientId, AppointmentStatus status);

    List<Appointment> findBySalonIdAndStatus(Long salonId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.hairdresserId = :hairdresserId " +
           "AND a.startTime >= :from AND a.endTime <= :to " +
           "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')")
    List<Appointment> findActiveByHairdresserAndDateRange(
            @Param("hairdresserId") Long hairdresserId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    long countBySalonId(Long salonId);
}
