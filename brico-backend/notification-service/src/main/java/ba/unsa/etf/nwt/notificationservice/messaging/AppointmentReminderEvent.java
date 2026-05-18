package ba.unsa.etf.nwt.notificationservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentReminderEvent {
    private Long          appointmentId;
    private Long          clientId;
    private String        clientEmail;
    private String        clientName;
    private String        hairdresserName;
    private String        salonName;
    private String        salonAddress;
    private LocalDateTime startTime;
    private BigDecimal    totalPrice;
    private String        servicesSummary;
    /** "24h" ili "1h" */
    private String        reminderType;
}
