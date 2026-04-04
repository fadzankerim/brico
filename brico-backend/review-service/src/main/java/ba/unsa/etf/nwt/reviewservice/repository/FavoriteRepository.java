package ba.unsa.etf.nwt.reviewservice.repository;

import ba.unsa.etf.nwt.reviewservice.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndSalonId(Long userId, Long salonId);

    boolean existsByUserIdAndSalonId(Long userId, Long salonId);

    void deleteByUserIdAndSalonId(Long userId, Long salonId);
}
