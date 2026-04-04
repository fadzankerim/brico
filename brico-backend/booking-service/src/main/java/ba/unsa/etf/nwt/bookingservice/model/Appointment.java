package ba.unsa.etf.nwt.bookingservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference na user-service i salon-service (bez JPA FK - mikroservis)
    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotBlank
    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "client_phone", length = 20)
    private String clientPhone;

    @NotNull
    @Column(name = "hairdresser_id", nullable = false)
    private Long hairdresserId;

    @NotBlank
    @Column(name = "hairdresser_name", nullable = false, length = 100)
    private String hairdresserName;

    @NotNull
    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @NotBlank
    @Column(name = "salon_name", nullable = false, length = 100)
    private String salonName;

    @Column(name = "salon_address", length = 200)
    private String salonAddress;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(length = 300)
    private String notes;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AppointmentItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
