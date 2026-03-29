package com.nwt.appointmentservice.repository;

import com.nwt.appointmentservice.model.Appointment;
import com.nwt.appointmentservice.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByClientId(Long clientId, Pageable pageable);

    Page<Appointment> findByHairdresserId(Long hairdresserId, Pageable pageable);

    Page<Appointment> findBySalonId(Long salonId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.salonId = :salonId " +
           "AND a.startTime >= :startDate AND a.startTime < :endDate")
    Page<Appointment> findBySalonIdAndDateRange(
            @Param("salonId") Long salonId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.hairdresserId = :hairdresserId " +
           "AND a.startTime >= :startDate AND a.startTime < :endDate")
    List<Appointment> findByHairdresserIdAndDateRange(
            @Param("hairdresserId") Long hairdresserId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Finds appointments that conflict with the proposed time slot for the given hairdresser.
     * A conflict exists when an existing non-cancelled appointment overlaps the [proposedStart, proposedEnd) window.
     */
    @Query("SELECT a FROM Appointment a WHERE a.hairdresserId = :hairdresserId " +
           "AND a.status != 'CANCELLED' " +
           "AND a.startTime < :endTime " +
           "AND a.endTime > :startTime")
    List<Appointment> findConflictingAppointments(
            @Param("hairdresserId") Long hairdresserId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT a FROM Appointment a WHERE a.hairdresserId = :hairdresserId " +
           "AND a.status != 'CANCELLED' " +
           "AND a.startTime >= :startDate AND a.startTime < :endDate")
    List<Appointment> findActiveByHairdresserAndDate(
            @Param("hairdresserId") Long hairdresserId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Appointment> findByStatusAndClientId(AppointmentStatus status, Long clientId);
}
