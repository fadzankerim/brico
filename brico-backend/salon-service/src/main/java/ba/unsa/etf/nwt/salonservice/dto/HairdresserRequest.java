package ba.unsa.etf.nwt.salonservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HairdresserRequest {

    private Long userId;

    @NotBlank(message = "Ime i prezime frizera su obavezni")
    @Size(max = 100)
    private String fullName;

    @Size(max = 300)
    private String bio;

    @Size(max = 200)
    private String specialties;

    private String profilePhoto;

    private Boolean isActive;
}
