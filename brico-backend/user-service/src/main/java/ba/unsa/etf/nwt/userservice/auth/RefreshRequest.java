package ba.unsa.etf.nwt.userservice.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotBlank(message = "Refresh token je obavezan")
    private String refreshToken;
}
