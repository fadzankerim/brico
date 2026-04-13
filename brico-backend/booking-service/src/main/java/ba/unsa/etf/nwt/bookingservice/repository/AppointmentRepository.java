package ba.unsa.etf.nwt.bookingservice.repository;

import ba.unsa.etf.nwt.bookingservice.model.Appointment;
import ba.unsa.etf.nwt.bookingservice.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findBySalonId(Long salonId);

    List<Appointment> findByHairdresserId(Long hairdresserId);

    List<Appointment> findByClientIdAndStatus(Long clientId, AppointmentStatus status);

    List<Appointment> findBySalonIdAndStatus(Long salonId, AppointmentStatus status);

    // Paginacija + sortiranje
    Page<Appointment> findAll(Pageable pageable);

    Page<Appointment> findBySalonId(Long salonId, Pageable pageable);

    Page<Appointment> findByClientId(Long clientId, Pageable pageable);

    // Custom JPQL — aktivni termini frizera u vremenskom rasponu
    @Query("SELECT a FROM Appointment a WHERE a.hairdresserId = :hairdresserId " +
           "AND a.startTime >= :from AND a.endTime <= :to " +
           "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')")
    List<Appointment> findActiveByHairdresserAndDateRange(
            @Param("hairdresserId") Long hairdresserId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // Statistika salona
    long countBySalonId(Long salonId);

    long countBySalonIdAndStatus(Long salonId, AppointmentStatus status);

    @Query("SELECT COALESCE(SUM(a.totalPrice), 0) FROM Appointment a " +
           "WHERE a.salonId = :salonId AND a.status = 'COMPLETED'")
    BigDecimal sumRevenueBySalon(@Param("salonId") Long salonId);

    // Termini u zadanom vremenskom prozoru za salon
    @Query("SELECT a FROM Appointment a WHERE a.salonId = :salonId " +
           "AND a.startTime >= :from AND a.startTime < :to")
    List<Appointment> findBySalonIdAndDateRange(
            @Param("salonId") Long salonId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
