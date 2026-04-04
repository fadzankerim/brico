package ba.unsa.etf.nwt.portfolioservice.service;

import ba.unsa.etf.nwt.portfolioservice.dto.*;
import ba.unsa.etf.nwt.portfolioservice.exception.DuplicateResourceException;
import ba.unsa.etf.nwt.portfolioservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.portfolioservice.model.*;
import ba.unsa.etf.nwt.portfolioservice.repository.SalonSubscriptionRepository;
import ba.unsa.etf.nwt.portfolioservice.repository.SubscriptionPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock SubscriptionPlanRepository planRepository;
    @Mock SalonSubscriptionRepository subscriptionRepository;
    @InjectMocks SubscriptionService subscriptionService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setup() throws Exception {
        var field = SubscriptionService.class.getDeclaredField("modelMapper");
        field.setAccessible(true);
        field.set(subscriptionService, modelMapper);
    }

    private SubscriptionPlan basicPlan() {
        return SubscriptionPlan.builder()
                .id(1L).planType(PlanType.BASIC).name("Basic")
                .priceKm(BigDecimal.ZERO).maxHairdressers(3)
                .hasAdvancedAnalytics(false).hasFeaturedStatus(false)
                .hasPrioritySupport(false).isActive(true).build();
    }

    private SubscriptionPlan proPlan() {
        return SubscriptionPlan.builder()
                .id(2L).planType(PlanType.PRO).name("Pro")
                .priceKm(new BigDecimal("50.00"))
                .hasAdvancedAnalytics(true).hasFeaturedStatus(true)
                .hasPrioritySupport(true).isActive(true).build();
    }

    private SalonSubscription sampleSubscription(SubscriptionPlan plan) {
        return SalonSubscription.builder()
                .id(1L).salonId(1L).plan(plan)
                .status(SubscriptionStatus.ACTIVE).build();
    }

    @Test
    void findAllPlans_returnsActivePlans() {
        when(planRepository.findByIsActive(true)).thenReturn(List.of(basicPlan(), proPlan()));
        List<PlanResponse> result = subscriptionService.findAllPlans();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPlanType()).isEqualTo(PlanType.BASIC);
    }

    @Test
    void findPlanById_existing_returnsPlan() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(basicPlan()));
        PlanResponse resp = subscriptionService.findPlanById(1L);
        assertThat(resp.getName()).isEqualTo("Basic");
    }

    @Test
    void findPlanById_unknown_throwsNotFound() {
        when(planRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> subscriptionService.findPlanById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void subscribe_newSalon_createsSubscription() {
        when(subscriptionRepository.findBySalonId(5L)).thenReturn(Optional.empty());
        when(planRepository.findByPlanType(PlanType.BASIC)).thenReturn(Optional.of(basicPlan()));
        SalonSubscription saved = sampleSubscription(basicPlan());
        saved.setSalonId(5L);
        when(subscriptionRepository.save(any())).thenReturn(saved);

        SubscriptionRequest req = new SubscriptionRequest();
        req.setSalonId(5L);
        req.setPlanType(PlanType.BASIC);

        SubscriptionResponse resp = subscriptionService.subscribe(req);
        assertThat(resp.getSalonId()).isEqualTo(5L);
        assertThat(resp.getPlan().getPlanType()).isEqualTo(PlanType.BASIC);
    }

    @Test
    void subscribe_existingSalon_throwsDuplicate() {
        when(subscriptionRepository.findBySalonId(1L))
                .thenReturn(Optional.of(sampleSubscription(basicPlan())));

        SubscriptionRequest req = new SubscriptionRequest();
        req.setSalonId(1L);
        req.setPlanType(PlanType.PRO);

        assertThatThrownBy(() -> subscriptionService.subscribe(req))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void changePlan_existingSubscription_updatesplan() {
        when(subscriptionRepository.findBySalonId(1L))
                .thenReturn(Optional.of(sampleSubscription(basicPlan())));
        when(planRepository.findByPlanType(PlanType.PRO)).thenReturn(Optional.of(proPlan()));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubscriptionResponse resp = subscriptionService.changePlan(1L, PlanType.PRO);
        assertThat(resp.getPlan().getPlanType()).isEqualTo(PlanType.PRO);
    }

    @Test
    void cancelSubscription_sets_cancelled() {
        SalonSubscription sub = sampleSubscription(basicPlan());
        when(subscriptionRepository.findBySalonId(1L)).thenReturn(Optional.of(sub));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubscriptionResponse resp = subscriptionService.cancelSubscription(1L);
        assertThat(resp.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }
}
