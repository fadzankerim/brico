package ba.unsa.etf.nwt.portfolioservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salon_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referenca na salon u salon-service
    @NotNull
    @Column(name = "salon_id", nullable = false, unique = true)
    private Long salonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.TRIAL;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Stripe identifikatori za Financial Service
    @Column(name = "stripe_subscription_id", length = 60)
    private String stripeSubscriptionId;

    @Column(name = "stripe_customer_id", length = 60)
    private String stripeCustomerId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
