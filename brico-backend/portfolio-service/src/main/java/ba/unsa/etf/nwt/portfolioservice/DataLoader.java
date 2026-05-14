package ba.unsa.etf.nwt.portfolioservice;

import ba.unsa.etf.nwt.portfolioservice.model.*;
import ba.unsa.etf.nwt.portfolioservice.repository.SalonSubscriptionRepository;
import ba.unsa.etf.nwt.portfolioservice.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

// Pretplate salona:
//  Salon 1 (Elite Cut)      → PRO  (aktivan 6 mj, još 6 mj)
//  Salon 2 (Urban Barber)   → PRO  (aktivan 2 mj, još 10 mj)
//  Salon 3 (Glam Studio)    → BASIC aktivan
//  Salon 4 (Barber Kingdom) → PRO  (aktivan 4 mj, još 8 mj)
//  Salon 5 (Style Lab)      → BASIC trial (14 dana)

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final SubscriptionPlanRepository  planRepository;
    private final SalonSubscriptionRepository subscriptionRepository;

    @Override
    public void run(String... args) {
        if (planRepository.count() > 0) {
            log.info("=== Portfolio Service: podaci već postoje, preskačem seed ===");
            return;
        }
        log.info("=== Seed: portfolio-service ===");

        // ── Planovi ──────────────────────────────────────────────────────
        SubscriptionPlan basicPlan = planRepository.save(
                SubscriptionPlan.builder()
                        .planType(PlanType.BASIC)
                        .name("BASIC")
                        .description("Besplatni plan za početnike. Idealan za male salone koji žele " +
                                "početi s digitalnom rezervacijom bez ikakvih troškova.")
                        .priceKm(BigDecimal.ZERO)
                        .maxHairdressers(3)
                        .hasAdvancedAnalytics(false)
                        .hasFeaturedStatus(false)
                        .hasPrioritySupport(false)
                        .stripePriceId(null)
                        .isActive(true)
                        .build()
        );

        SubscriptionPlan proPlan = planRepository.save(
                SubscriptionPlan.builder()
                        .planType(PlanType.PRO)
                        .name("PRO")
                        .description("Profesionalni plan za ambiciozne salone. Neograničen broj frizera, " +
                                "napredna analitika i prioritetna podrška.")
                        .priceKm(new BigDecimal("50.00"))
                        .maxHairdressers(null)
                        .hasAdvancedAnalytics(true)
                        .hasFeaturedStatus(true)
                        .hasPrioritySupport(true)
                        .stripePriceId("price_brico_pro_monthly")
                        .isActive(true)
                        .build()
        );

        // ── Pretplate ─────────────────────────────────────────────────────

        // Salon 1 — Elite Cut → PRO (aktivan 6 mjeseci)
        subscriptionRepository.save(SalonSubscription.builder()
                .salonId(1L)
                .plan(proPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now().minusMonths(6))
                .endDate(LocalDate.now().plusMonths(6))
                .stripeCustomerId("cus_elite_cut_brico")
                .stripeSubscriptionId("sub_elite_cut_brico")
                .build());

        // Salon 2 — Urban Barber → PRO (aktivan 2 mjeseca)
        subscriptionRepository.save(SalonSubscription.builder()
                .salonId(2L)
                .plan(proPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now().minusMonths(2))
                .endDate(LocalDate.now().plusMonths(10))
                .stripeCustomerId("cus_urban_barber_brico")
                .stripeSubscriptionId("sub_urban_barber_brico")
                .build());

        // Salon 3 — Glam Studio → BASIC (aktivan)
        subscriptionRepository.save(SalonSubscription.builder()
                .salonId(3L)
                .plan(basicPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now().minusMonths(1))
                .build());

        // Salon 4 — Barber Kingdom → PRO (aktivan 4 mjeseca)
        subscriptionRepository.save(SalonSubscription.builder()
                .salonId(4L)
                .plan(proPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now().minusMonths(4))
                .endDate(LocalDate.now().plusMonths(8))
                .stripeCustomerId("cus_barber_kingdom_brico")
                .stripeSubscriptionId("sub_barber_kingdom_brico")
                .build());

        // Salon 5 — Style Lab → BASIC trial (14 dana, ističe za 16 dana)
        subscriptionRepository.save(SalonSubscription.builder()
                .salonId(5L)
                .plan(basicPlan)
                .status(SubscriptionStatus.TRIAL)
                .startDate(LocalDate.now().minusDays(14))
                .endDate(LocalDate.now().plusDays(16))
                .build());

        log.info("Kreirani planovi: BASIC (0 KM) i PRO (50 KM)");
        log.info("Kreirano {} pretplata: 3xPRO, 2xBASIC", subscriptionRepository.count());
        log.info("=== Portfolio Service seed završen ===");
    }
}
