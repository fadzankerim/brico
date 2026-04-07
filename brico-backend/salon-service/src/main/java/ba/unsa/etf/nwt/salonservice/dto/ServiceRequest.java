package ba.unsa.etf.nwt.salonservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceRequest {

    @NotBlank(message = "Naziv usluge je obavezan")
    @Size(max = 100)
    private String name;

    @Size(max = 300)
    private String description;

    @NotNull(message = "Cijena je obavezna")
    @DecimalMin(value = "0.0", message = "Cijena ne može biti negativna")
    private BigDecimal price;

    @NotNull(message = "Trajanje je obavezno")
    @Min(value = 5, message = "Trajanje mora biti najmanje 5 minuta")
    private Integer durationMinutes;
}
