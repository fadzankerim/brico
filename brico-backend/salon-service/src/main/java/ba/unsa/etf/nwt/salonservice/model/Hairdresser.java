package ba.unsa.etf.nwt.salonservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "hairdressers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hairdresser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referenca na korisnika u user-service
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Ime i prezime frizera su obavezni")
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Size(max = 300)
    @Column(length = 300)
    private String bio;

    // Specijalizacije (npr. "Bojenje,Šišanje,Pramenovi")
    @Column(length = 200)
    private String specialties;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Salon salon;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
