package ba.unsa.etf.nwt.salonservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "salons",
    uniqueConstraints = @UniqueConstraint(name = "uq_salons_slug", columnNames = "slug"),
    indexes = {
        @Index(name = "idx_salons_owner",    columnList = "owner_id"),
        @Index(name = "idx_salons_city",     columnList = "city"),
        @Index(name = "idx_salons_active",   columnList = "is_active"),
        @Index(name = "idx_salons_verified", columnList = "verified")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Naziv salona je obavezan")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Slug je obavezan")
    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @NotBlank(message = "Grad je obavezan")
    @Column(nullable = false, length = 60)
    private String city;

    @NotBlank(message = "Adresa je obavezna")
    @Column(nullable = false, length = 200)
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String website;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Cached/denormalizovano za brzo filtriranje bez join-a prema review-service
    @Column(name = "avg_rating", nullable = false)
    @Builder.Default
    private Double avgRating = 0.0;

    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;

    // Referenca na vlasnika u user-service
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // Referenca na subscription plan u portfolio-service
    @Column(name = "subscription_plan_id")
    private Long subscriptionPlanId;

    @Column(name = "stripe_customer_id", length = 60)
    private String stripeCustomerId;

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Hairdresser> hairdressers = new ArrayList<>();

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SalonService> services = new ArrayList<>();

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<WorkingHours> workingHours = new ArrayList<>();

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SalonPhoto> photos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
