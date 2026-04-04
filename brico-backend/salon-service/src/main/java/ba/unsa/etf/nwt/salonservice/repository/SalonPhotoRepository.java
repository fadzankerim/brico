package ba.unsa.etf.nwt.salonservice.repository;

import ba.unsa.etf.nwt.salonservice.model.SalonPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalonPhotoRepository extends JpaRepository<SalonPhoto, Long> {
    List<SalonPhoto> findBySalonId(Long salonId);
    Optional<SalonPhoto> findBySalonIdAndIsPrimary(Long salonId, Boolean isPrimary);
}
