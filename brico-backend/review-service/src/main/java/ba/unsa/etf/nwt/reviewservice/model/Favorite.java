package ba.unsa.etf.nwt.reviewservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = @UniqueConstraint(name = "uq_favorites_user_salon", columnNames = {"user_id", "salon_id"}),
    indexes = {
        @Index(name = "idx_favorites_user",  columnList = "user_id"),
        @Index(name = "idx_favorites_salon", columnList = "salon_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "salon_name", length = 100)
    private String salonName;

    @Column(name = "salon_slug", length = 120)
    private String salonSlug;

    @Column(name = "salon_city", length = 60)
    private String salonCity;

    @Column(name = "salon_avg_rating")
    private Double salonAvgRating;

    @Column(name = "salon_verified")
    private Boolean salonVerified;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
