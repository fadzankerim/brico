package ba.unsa.etf.nwt.salonservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "services",
    indexes = {
        @Index(name = "idx_services_salon",  columnList = "salon_id"),
        @Index(name = "idx_services_active", columnList = "is_active")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Naziv usluge je obavezan")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 300)
    @Column(length = 300)
    private String description;

    @NotNull(message = "Cijena je obavezna")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cijena mora biti veća od 0")
    //@Column(nullable = false, precision = 10, scale = 7)
    @Column()
    private BigDecimal price;

    @NotNull(message = "Trajanje je obavezno")
    @Min(value = 5, message = "Minimalno trajanje je 5 minuta")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Salon salon;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
