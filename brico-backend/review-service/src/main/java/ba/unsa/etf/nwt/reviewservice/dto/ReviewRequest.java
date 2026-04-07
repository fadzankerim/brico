package ba.unsa.etf.nwt.reviewservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "Client ID je obavezan")
    private Long clientId;

    @NotBlank(message = "Ime klijenta je obavezno")
    private String clientName;

    private String clientPhoto;

    @NotNull(message = "Salon ID je obavezan")
    private Long salonId;

    private Long hairdresserId;
    private String hairdresserName;
    private Long appointmentId;

    @NotNull(message = "Ocjena je obavezna")
    @Min(value = 1, message = "Ocjena mora biti između 1 i 5")
    @Max(value = 5, message = "Ocjena mora biti između 1 i 5")
    private Integer rating;

    @Size(max = 500, message = "Komentar ne smije biti duži od 500 karaktera")
    private String comment;
}
