package ba.unsa.etf.nwt.salonservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SalonRequest {

    @NotBlank(message = "Naziv salona je obavezan")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotBlank(message = "Grad je obavezan")
    @Size(max = 60)
    private String city;

    @NotBlank(message = "Adresa je obavezna")
    @Size(max = 200)
    private String address;

    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    @Size(max = 20)
    private String phone;

    @Size(max = 200)
    private String website;

    @NotBlank(message = "Identifikacioni broj je obavezan")
    @Pattern(regexp = "^\\d{13}$", message = "Identifikacioni broj mora imati tačno 13 cifara")
    private String idBroj;

    @NotNull(message = "OwnerId je obavezan")
    private Long ownerId;
}
