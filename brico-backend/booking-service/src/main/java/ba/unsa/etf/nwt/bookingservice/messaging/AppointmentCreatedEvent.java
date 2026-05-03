package ba.unsa.etf.nwt.bookingservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreatedEvent {
    private Long          appointmentId;
    private Long          hairdresserId;
    private Long          salonId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long          clientId;
}
