package ba.unsa.etf.nwt.bookingservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AppointmentItemRequest {

    @NotNull(message = "Service ID je obavezan")
    private Long serviceId;

    @NotBlank(message = "Naziv usluge je obavezan")
    private String serviceName;

    @NotNull(message = "Cijena je obavezna")
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull(message = "Trajanje je obavezno")
    @Min(5)
    private Integer durationMinutes;
}
