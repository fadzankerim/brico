package ba.unsa.etf.nwt.portfolioservice.service;

import ba.unsa.etf.nwt.portfolioservice.dto.*;
import ba.unsa.etf.nwt.portfolioservice.exception.DuplicateResourceException;
import ba.unsa.etf.nwt.portfolioservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.portfolioservice.model.*;
import ba.unsa.etf.nwt.portfolioservice.repository.SalonSubscriptionRepository;
import ba.unsa.etf.nwt.portfolioservice.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final SalonSubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;

    // ── Plans ──────────────────────────────────────────────────────────

    public List<PlanResponse> findAllPlans() {
        return planRepository.findByIsActive(true).stream()
                .map(p -> modelMapper.map(p, PlanResponse.class)).toList();
    }

    public PlanResponse findPlanById(Long id) {
        return modelMapper.map(getPlanOrThrow(id), PlanResponse.class);
    }

    // ── Subscriptions ─────────────────────────────────────────────────

    public List<SubscriptionResponse> findAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(this::toSubResponse).toList();
    }

    public SubscriptionResponse findBySalonId(Long salonId) {
        SalonSubscription sub = subscriptionRepository.findBySalonId(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Pretplata za salon ID=" + salonId + " nije pronađena"));
        return toSubResponse(sub);
    }

    @Transactional
    public SubscriptionResponse subscribe(SubscriptionRequest req) {
        // Each salon can only have one subscription — upgrade/downgrade allowed
        subscriptionRepository.findBySalonId(req.getSalonId()).ifPresent(existing -> {
            throw new DuplicateResourceException("Salon već ima aktivnu pretplatu. Koristite update endpoint.");
        });

        SubscriptionPlan plan = planRepository.findByPlanType(req.getPlanType())
                .orElseThrow(() -> new ResourceNotFoundException("Plan '" + req.getPlanType() + "' nije pronađen"));

        SalonSubscription sub = SalonSubscription.builder()
                .salonId(req.getSalonId())
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .stripeSubscriptionId(req.getStripeSubscriptionId())
                .stripeCustomerId(req.getStripeCustomerId())
                .build();

        return toSubResponse(subscriptionRepository.save(sub));
    }

    @Transactional
    public SubscriptionResponse changePlan(Long salonId, PlanType newPlanType) {
        SalonSubscription sub = subscriptionRepository.findBySalonId(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Pretplata za salon ID=" + salonId + " nije pronađena"));

        SubscriptionPlan plan = planRepository.findByPlanType(newPlanType)
                .orElseThrow(() -> new ResourceNotFoundException("Plan '" + newPlanType + "' nije pronađen"));

        sub.setPlan(plan);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        return toSubResponse(subscriptionRepository.save(sub));
    }

    @Transactional
    public SubscriptionResponse cancelSubscription(Long salonId) {
        SalonSubscription sub = subscriptionRepository.findBySalonId(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Pretplata za salon ID=" + salonId + " nije pronađena"));
        sub.setStatus(SubscriptionStatus.CANCELLED);
        sub.setEndDate(LocalDate.now());
        return toSubResponse(subscriptionRepository.save(sub));
    }

    private SubscriptionPlan getPlanOrThrow(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan sa ID=" + id + " nije pronađen"));
    }

    private SubscriptionResponse toSubResponse(SalonSubscription sub) {
        SubscriptionResponse r = new SubscriptionResponse();
        r.setId(sub.getId());
        r.setSalonId(sub.getSalonId());
        r.setPlan(modelMapper.map(sub.getPlan(), PlanResponse.class));
        r.setStatus(sub.getStatus());
        r.setStartDate(sub.getStartDate());
        r.setEndDate(sub.getEndDate());
        r.setStripeSubscriptionId(sub.getStripeSubscriptionId());
        r.setStripeCustomerId(sub.getStripeCustomerId());
        r.setCreatedAt(sub.getCreatedAt());
        return r;
    }
}
