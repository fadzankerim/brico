package ba.unsa.etf.nwt.portfolioservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "subscription_plans",
    indexes = {
        @Index(name = "idx_plan_type",   columnList = "plan_type"),
        @Index(name = "idx_plan_active", columnList = "is_active")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private PlanType planType;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    @Size(max = 300)
    @Column(length = 300)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "price_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceKm;

    @Column(name = "max_hairdressers")
    private Integer maxHairdressers;

    @Column(name = "has_advanced_analytics", nullable = false)
    @Builder.Default
    private Boolean hasAdvancedAnalytics = false;

    @Column(name = "has_featured_status", nullable = false)
    @Builder.Default
    private Boolean hasFeaturedStatus = false;

    @Column(name = "has_priority_support", nullable = false)
    @Builder.Default
    private Boolean hasPrioritySupport = false;

    @Column(name = "stripe_price_id", length = 60)
    private String stripePriceId;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SalonSubscription> subscriptions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
