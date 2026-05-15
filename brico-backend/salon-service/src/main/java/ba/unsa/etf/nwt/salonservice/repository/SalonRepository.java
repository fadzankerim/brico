package ba.unsa.etf.nwt.salonservice.repository;

import ba.unsa.etf.nwt.salonservice.model.Salon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {

    Optional<Salon> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByIdBroj(String idBroj);

    boolean existsByIdBrojAndIdNot(String idBroj, Long id);

    List<Salon> findByCity(String city);

    List<Salon> findByIsActive(Boolean isActive);

    List<Salon> findByOwnerId(Long ownerId);

    List<Salon> findByCityAndIsActive(String city, Boolean isActive);

    // Paginacija + sortiranje
    Page<Salon> findAll(Pageable pageable);

    Page<Salon> findByCity(String city, Pageable pageable);

    // Custom JPQL pretraga po gradu i minimalnoj ocjeni
    @Query("SELECT s FROM Salon s WHERE s.isActive = true " +
           "AND (:city IS NULL OR LOWER(s.city) = LOWER(:city)) " +
           "AND (:minRating IS NULL OR s.avgRating >= :minRating) " +
           "ORDER BY s.avgRating DESC")
    List<Salon> searchByCityAndRating(@Param("city") String city,
                                      @Param("minRating") Double minRating);

    // Full-text pretraga po imenu ili gradu
    @Query("SELECT s FROM Salon s WHERE s.isActive = true AND (" +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.city) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Salon> searchByNameOrCity(@Param("q") String q, Pageable pageable);
}
