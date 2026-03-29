package com.nwt.salonservice.repository;

import com.nwt.salonservice.model.Hairdresser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HairdresserRepository extends JpaRepository<Hairdresser, Long> {

    List<Hairdresser> findBySalonId(Long salonId);

    @Query("SELECT h FROM Hairdresser h WHERE h.id IN :ids")
    List<Hairdresser> findAllByIdIn(@Param("ids") List<Long> ids);
}
