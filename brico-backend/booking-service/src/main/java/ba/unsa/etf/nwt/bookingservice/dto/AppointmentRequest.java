package ba.unsa.etf.nwt.bookingservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AppointmentRequest {

    @NotNull(message = "Client ID je obavezan")
    private Long clientId;

    @NotBlank(message = "Ime klijenta je obavezno")
    private String clientName;

    private String clientPhone;

    @NotNull(message = "Hairdresser ID je obavezan")
    private Long hairdresserId;

    @NotBlank(message = "Ime frizera je obavezno")
    private String hairdresserName;

    @NotNull(message = "Salon ID je obavezan")
    private Long salonId;

    @NotBlank(message = "Naziv salona je obavezan")
    private String salonName;

    private String salonAddress;

    @NotNull(message = "Početak termina je obavezan")
    @Future(message = "Termin mora biti u budućnosti")
    private LocalDateTime startTime;

    @Size(max = 300)
    private String notes;

    @NotEmpty(message = "Termin mora imati barem jednu uslugu")
    @Valid
    private List<AppointmentItemRequest> items;
}
