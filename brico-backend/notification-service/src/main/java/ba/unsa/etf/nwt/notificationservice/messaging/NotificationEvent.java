package ba.unsa.etf.nwt.notificationservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private Long   userId;
    private String type;
    private String title;
    private String message;
    private Long   referenceId;
    private String recipientEmail;

    public NotificationEvent(Long userId, String type, String title, String message, Long referenceId) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.referenceId = referenceId;
    }
}
