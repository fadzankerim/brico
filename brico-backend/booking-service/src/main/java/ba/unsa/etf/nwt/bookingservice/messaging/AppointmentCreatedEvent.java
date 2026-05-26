package ba.unsa.etf.nwt.bookingservice.messaging;

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
public class AppointmentCreatedEvent {
    private Long          appointmentId;
    private Long          hairdresserId;
    private Long          salonId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long          clientId;

    // Podaci za email notifikacije
    private String        clientEmail;
    private String        clientName;
    private String        hairdresserName;
    private String        salonName;
    private String        salonAddress;
    private BigDecimal    totalPrice;
    private String        servicesSummary;
}
