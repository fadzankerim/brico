package ba.unsa.etf.nwt.salonservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "hairdressers",
    indexes = {
        @Index(name = "idx_hairdressers_salon",  columnList = "salon_id"),
        @Index(name = "idx_hairdressers_user",   columnList = "user_id"),
        @Index(name = "idx_hairdressers_active", columnList = "is_active")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hairdresser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Ime i prezime frizera su obavezni")
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Size(max = 300)
    @Column(length = 300)
    private String bio;

    @Column(length = 200)
    private String specialties;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Cached prosječna ocjena (ažurira se kada se doda recenzija)
    @Column(name = "avg_rating", nullable = false)
    @Builder.Default
    private Double avgRating = 0.0;

    @Column(name = "total_appointments", nullable = false)
    @Builder.Default
    private Integer totalAppointments = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Salon salon;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
