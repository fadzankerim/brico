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
}
