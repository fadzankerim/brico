package ba.unsa.etf.nwt.salonservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salons",
        uniqueConstraints = @UniqueConstraint(columnNames = "slug"))
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

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    @Column(precision = 10, scale = 7)
    private Double latitude;

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    @Column(precision = 10, scale = 7)
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

    // Referenca na vlasnika u user-service (bez FK jer je drugi servis)
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // Referenca na subscription plan u portfolio-service
    @Column(name = "subscription_plan_id")
    private Long subscriptionPlanId;

    // Stripe integracija (Financial Service)
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
}
