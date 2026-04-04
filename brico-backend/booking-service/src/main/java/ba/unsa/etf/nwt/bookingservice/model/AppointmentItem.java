package ba.unsa.etf.nwt.bookingservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Stavka termina - predstavlja jednu uslugu u okviru termina (višestruke usluge).
 * Naziv i cijena se denormalizuju radi nezavisnosti od salon-service.
 */
@Entity
@Table(name = "appointment_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID usluge iz salon-service (logička referenca)
    @NotNull
    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    // Denormalizovani naziv usluge (snapshot u trenutku bookinga)
    @NotBlank
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Min(5)
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Appointment appointment;
}
