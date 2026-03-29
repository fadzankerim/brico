package com.nwt.salonservice.repository;

import com.nwt.salonservice.model.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    List<WorkingHours> findByHairdresserId(Long hairdresserId);
}
