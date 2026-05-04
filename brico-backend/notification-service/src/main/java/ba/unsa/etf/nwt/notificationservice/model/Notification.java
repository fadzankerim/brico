package ba.unsa.etf.nwt.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notif_user", columnList = "user_id"),
    @Index(name = "idx_notif_read", columnList = "user_id, is_read")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String type;  // APPOINTMENT_CONFIRMED, APPOINTMENT_CANCELLED, NEW_APPOINTMENT, NEW_REVIEW

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 300)
    private String message;

    @Column(name = "reference_id")
    private Long referenceId;  // appointmentId ili reviewId

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
