package ba.unsa.etf.nwt.eventsservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemEvent {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    @Column(name = "service_name", nullable = false, length = 60)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 20)
    private ActionType actionType;

    @Column(name = "resource_name", length = 60)
    private String resourceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_type", nullable = false, length = 20)
    private ResponseType responseType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "user_id", length = 60)
    private String userId;

    @Column(length = 500)
    private String details;

    public enum ActionType  { CREATE, DELETE, GET, UPDATE }
    public enum ResponseType { SUCCESS, ERROR }
}
