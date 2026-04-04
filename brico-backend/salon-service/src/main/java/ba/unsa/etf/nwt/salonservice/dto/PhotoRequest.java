package ba.unsa.etf.nwt.salonservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PhotoRequest {

    @NotBlank(message = "URL fotografije je obavezan")
    private String url;

    private Boolean isPrimary = false;

    private Integer displayOrder = 0;
}
