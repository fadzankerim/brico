package ba.unsa.etf.nwt.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCredentialsRequest {

    @Email(message = "Email mora biti validan")
    private String email;

    @Size(min = 8, message = "Lozinka mora imati najmanje 8 karaktera")
    private String password;
}
