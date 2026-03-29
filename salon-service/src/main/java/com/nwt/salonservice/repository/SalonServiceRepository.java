package com.nwt.salonservice.repository;

import com.nwt.salonservice.model.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {

    List<SalonService> findBySalonId(Long salonId);

    List<SalonService> findBySalonIdAndIsActiveTrue(Long salonId);

    @Query("SELECT ss FROM SalonService ss WHERE ss.id IN :ids")
    List<SalonService> findAllByIdIn(@Param("ids") List<Long> ids);
}
