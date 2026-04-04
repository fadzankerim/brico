package ba.unsa.etf.nwt.reviewservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
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

    // Opcionalno - recenzija može biti za specifičnog frizera
    @Column(name = "hairdresser_id")
    private Long hairdresserId;

    @Column(name = "hairdresser_name", length = 100)
    private String hairdresserName;

    // Opcionalno - vezano za konkretan termin
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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
