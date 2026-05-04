package ba.unsa.etf.nwt.salonservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booked_slots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false, unique = true)
    private Long appointmentId;

    @Column(name = "hairdresser_id", nullable = false)
    private Long hairdresserId;

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SlotStatus status;

    public enum SlotStatus { RESERVED, CANCELLED }
}
