package ba.unsa.etf.nwt.portfolioservice.repository;

import ba.unsa.etf.nwt.portfolioservice.model.SalonSubscription;
import ba.unsa.etf.nwt.portfolioservice.model.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalonSubscriptionRepository extends JpaRepository<SalonSubscription, Long> {

    Optional<SalonSubscription> findBySalonId(Long salonId);

    List<SalonSubscription> findByStatus(SubscriptionStatus status);

    List<SalonSubscription> findByPlanId(Long planId);

    // @EntityGraph — eager loading plan-a zajedno sa pretplatom (izbjegava N+1 problem)
    @EntityGraph(attributePaths = {"plan"})
    Optional<SalonSubscription> findWithPlanBySalonId(Long salonId);

    @EntityGraph(attributePaths = {"plan"})
    List<SalonSubscription> findAllWithPlanByStatus(SubscriptionStatus status);

    // Paginacija sa eager loadingom plana
    @EntityGraph(attributePaths = {"plan"})
    Page<SalonSubscription> findAll(Pageable pageable);

    // Statistika
    @Query("SELECT COUNT(s) FROM SalonSubscription s WHERE s.plan.id = :planId AND s.status = 'ACTIVE'")
    long countActiveByPlanId(@Param("planId") Long planId);

    @Query("SELECT s.status, COUNT(s) FROM SalonSubscription s GROUP BY s.status")
    List<Object[]> countGroupByStatus();

    @Query("SELECT s.plan.planType, COUNT(s) FROM SalonSubscription s WHERE s.status = 'ACTIVE' GROUP BY s.plan.planType")
    List<Object[]> countActiveGroupByPlanType();
}
