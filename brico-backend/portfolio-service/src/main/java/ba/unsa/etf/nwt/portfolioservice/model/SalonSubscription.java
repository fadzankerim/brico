package ba.unsa.etf.nwt.portfolioservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "salon_subscriptions",
    uniqueConstraints = @UniqueConstraint(name = "uq_subscription_salon", columnNames = "salon_id"),
    indexes = {
        @Index(name = "idx_sub_salon",  columnList = "salon_id"),
        @Index(name = "idx_sub_status", columnList = "status"),
        @Index(name = "idx_sub_plan",   columnList = "plan_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "stripe_subscription_id", length = 60)
    private String stripeSubscriptionId;

    @Column(name = "stripe_customer_id", length = 60)
    private String stripeCustomerId;

    @Column(name = "cancel_reason", length = 300)
    private String cancelReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
