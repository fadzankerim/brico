package ba.unsa.etf.nwt.bookingservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RescheduleRequest {

    @NotNull(message = "Novi termin je obavezan")
    @Future(message = "Termin mora biti u budućnosti")
    private LocalDateTime newStartTime;

    private String reason;
}
