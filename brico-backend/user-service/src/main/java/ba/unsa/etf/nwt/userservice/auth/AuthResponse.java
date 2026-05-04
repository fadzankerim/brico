package ba.unsa.etf.nwt.userservice.auth;

import ba.unsa.etf.nwt.userservice.dto.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long   expiresIn;   // sekunde do isteka access tokena
    private UserResponse user;
}
