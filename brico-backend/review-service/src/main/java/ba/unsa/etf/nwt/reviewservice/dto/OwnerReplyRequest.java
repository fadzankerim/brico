package ba.unsa.etf.nwt.reviewservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OwnerReplyRequest {

    @NotBlank(message = "Odgovor vlasnika je obavezan")
    @Size(max = 500)
    private String reply;
}
