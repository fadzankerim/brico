package ba.unsa.etf.nwt.salonservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotResponseEvent {
    private Long       appointmentId;
    private String     reason;
    private Long       clientId;
    private String     salonName;
    private String     startTime;

    // Podaci za email notifikacije
    private String     clientEmail;
    private String     clientName;
    private Long       hairdresserUserId;
    private String     hairdresserName;
    private Long       salonOwnerId;
    private String     salonAddress;
    private BigDecimal totalPrice;
    private String     servicesSummary;
}
