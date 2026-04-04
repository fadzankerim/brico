package ba.unsa.etf.nwt.portfolioservice.repository;

import ba.unsa.etf.nwt.portfolioservice.model.SalonSubscription;
import ba.unsa.etf.nwt.portfolioservice.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalonSubscriptionRepository extends JpaRepository<SalonSubscription, Long> {

    Optional<SalonSubscription> findBySalonId(Long salonId);

    List<SalonSubscription> findByStatus(SubscriptionStatus status);

    List<SalonSubscription> findByPlanId(Long planId);

    @Query("SELECT COUNT(s) FROM SalonSubscription s WHERE s.plan.id = :planId AND s.status = 'ACTIVE'")
    long countActiveByPlanId(Long planId);
}
