package ba.unsa.etf.nwt.reviewservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "reviews",
    indexes = {
        @Index(name = "idx_reviews_salon",       columnList = "salon_id"),
        @Index(name = "idx_reviews_client",      columnList = "client_id"),
        @Index(name = "idx_reviews_hairdresser", columnList = "hairdresser_id"),
        @Index(name = "idx_reviews_rating",      columnList = "salon_id,rating"),
        @Index(name = "idx_reviews_appointment", columnList = "appointment_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotBlank
    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "client_photo")
    private String clientPhoto;

    @NotNull
    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "hairdresser_id")
    private Long hairdresserId;

    @Column(name = "hairdresser_name", length = 100)
    private String hairdresserName;

    @Column(name = "appointment_id")
    private Long appointmentId;

    @NotNull
    @Min(value = 1, message = "Ocjena mora biti između 1 i 5")
    @Max(value = 5, message = "Ocjena mora biti između 1 i 5")
    @Column(nullable = false)
    private Integer rating;

    @Size(max = 500)
    @Column(length = 500)
    private String comment;

    // Vlasnik salona može odgovoriti na recenziju
    @Size(max = 500)
    @Column(name = "owner_reply", length = 500)
    private String ownerReply;

    @Column(name = "owner_reply_at")
    private LocalDateTime ownerReplyAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
