package ba.unsa.etf.nwt.portfolioservice.controller;

import ba.unsa.etf.nwt.portfolioservice.dto.*;
import ba.unsa.etf.nwt.portfolioservice.model.PlanType;
import ba.unsa.etf.nwt.portfolioservice.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Pretplatni planovi i salon pretplate")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // ── Plans ──────────────────────────────────────────────────────────

    @GetMapping("/plans")
    @Operation(summary = "Dohvati sve aktivne planove")
    public ResponseEntity<List<PlanResponse>> getPlans() {
        return ResponseEntity.ok(subscriptionService.findAllPlans());
    }

    @GetMapping("/plans/{id}")
    @Operation(summary = "Dohvati plan po ID-u")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.findPlanById(id));
    }

    // ── Subscriptions ─────────────────────────────────────────────────

    @GetMapping("/subscriptions")
    @Operation(summary = "Dohvati sve pretplate")
    public ResponseEntity<List<SubscriptionResponse>> getAll() {
        return ResponseEntity.ok(subscriptionService.findAllSubscriptions());
    }

    @GetMapping("/subscriptions/salon/{salonId}")
    @Operation(summary = "Dohvati pretplatu salona")
    public ResponseEntity<SubscriptionResponse> getBySalon(@PathVariable Long salonId) {
        return ResponseEntity.ok(subscriptionService.findBySalonId(salonId));
    }

    @PostMapping("/subscriptions")
    @Operation(summary = "Pretplati salon na plan")
    public ResponseEntity<SubscriptionResponse> subscribe(@Valid @RequestBody SubscriptionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.subscribe(req));
    }

    @PatchMapping("/subscriptions/salon/{salonId}/plan")
    @Operation(summary = "Promijeni plan salona")
    public ResponseEntity<SubscriptionResponse> changePlan(@PathVariable Long salonId,
                                                           @RequestParam PlanType planType) {
        return ResponseEntity.ok(subscriptionService.changePlan(salonId, planType));
    }

    @PatchMapping("/subscriptions/salon/{salonId}/cancel")
    @Operation(summary = "Otkaži pretplatu salona")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable Long salonId) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(salonId));
    }
}
