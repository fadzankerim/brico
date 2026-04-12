package ba.unsa.etf.nwt.portfolioservice.repository;

import ba.unsa.etf.nwt.portfolioservice.model.PlanType;
import ba.unsa.etf.nwt.portfolioservice.model.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findByPlanType(PlanType planType);

    List<SubscriptionPlan> findByIsActive(Boolean isActive);

    // Paginacija aktivnih planova
    Page<SubscriptionPlan> findByIsActive(Boolean isActive, Pageable pageable);
}
