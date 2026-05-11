package ba.unsa.etf.nwt.salonservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "hairdresser_unavailability", indexes = {
    @Index(name = "idx_unavail_hairdresser", columnList = "hairdresser_id"),
    @Index(name = "idx_unavail_time", columnList = "start_time, end_time")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HairdresserUnavailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hairdresser_id", nullable = false)
    private Long hairdresserId;

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(length = 200)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
