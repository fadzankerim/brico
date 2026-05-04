package ba.unsa.etf.nwt.bookingservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponseEvent {
    private Long   appointmentId;
    private String reason; // popunjeno samo kod rejection
}
