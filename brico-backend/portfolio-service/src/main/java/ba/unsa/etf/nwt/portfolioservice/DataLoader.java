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

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final SubscriptionPlanRepository planRepository;
    private final SalonSubscriptionRepository subscriptionRepository;

    @Override
    public void run(String... args) {
        if (planRepository.count() > 0) {
            log.info("=== Portfolio Service: podaci već postoje, preskačem seed ===");
            return;
        }
        log.info("=== Učitavanje početnih podataka za Portfolio Service ===");

        // ── BASIC plan ───────────────────────────────────────────
        SubscriptionPlan basicPlan = planRepository.save(
                SubscriptionPlan.builder()
                        .planType(PlanType.BASIC)
                        .name("BASIC")
                        .description("Besplatni plan za početnike. Idealan za male salone koji žele " +
                                "početi s digitalnom rezervacijom.")
                        .priceKm(BigDecimal.ZERO)
                        .maxHairdressers(3)           // Ograničenje: max 3 frizera
                        .hasAdvancedAnalytics(false)  // Bez napredne analitike
                        .hasFeaturedStatus(false)     // Bez featured statusa
                        .hasPrioritySupport(false)
                        .stripePriceId(null)          // Besplatan - nema Stripe cijene
                        .isActive(true)
                        .build()
        );

        // ── PRO plan ─────────────────────────────────────────────
        SubscriptionPlan proPlan = planRepository.save(
                SubscriptionPlan.builder()
                        .planType(PlanType.PRO)
                        .name("PRO")
                        .description("Profesionalni plan za ambiciozne salone. Neograničen broj frizera, " +
                                "napredna analitika i prioritetna podrška.")
                        .priceKm(new BigDecimal("50.00"))
                        .maxHairdressers(null)        // Neograničeno
                        .hasAdvancedAnalytics(true)   // Napredna analitika
                        .hasFeaturedStatus(true)      // Featured u pretrazi
                        .hasPrioritySupport(true)
                        .stripePriceId("price_brico_pro_monthly") // Placeholder Stripe Price ID
                        .isActive(true)
                        .build()
        );

        log.info("Kreirani planovi: BASIC ({}  KM) i PRO ({} KM)",
                basicPlan.getPriceKm(), proPlan.getPriceKm());

        // ── Pretplate salona ──────────────────────────────────────
        // Salon 1 → PRO plan (aktivan)
        subscriptionRepository.save(
                SalonSubscription.builder()
                        .salonId(1L)
                        .plan(proPlan)
                        .status(SubscriptionStatus.ACTIVE)
                        .startDate(LocalDate.now().minusMonths(3))
                        .endDate(LocalDate.now().plusMonths(9))
                        .stripeCustomerId("cus_salon1_placeholder")
                        .stripeSubscriptionId("sub_salon1_placeholder")
                        .build()
        );

        // Salon 2 → BASIC plan (aktivan - trial)
        subscriptionRepository.save(
                SalonSubscription.builder()
                        .salonId(2L)
                        .plan(basicPlan)
                        .status(SubscriptionStatus.ACTIVE)
                        .startDate(LocalDate.now().minusMonths(1))
                        .build()
        );

        // Salon 3 → BASIC plan (trial period)
        subscriptionRepository.save(
                SalonSubscription.builder()
                        .salonId(3L)
                        .plan(basicPlan)
                        .status(SubscriptionStatus.TRIAL)
                        .startDate(LocalDate.now().minusDays(7))
                        .endDate(LocalDate.now().plusDays(23)) // 30 dana trial
                        .build()
        );

        log.info("Kreirano {} pretplata salona", subscriptionRepository.count());
        log.info("Aktivnih PRO pretplata: {}", subscriptionRepository.countActiveByPlanId(proPlan.getId()));
        log.info("Aktivnih BASIC pretplata: {}", subscriptionRepository.countActiveByPlanId(basicPlan.getId()));
        log.info("=== Portfolio Service inicijalizacija završena ===");
    }
}
