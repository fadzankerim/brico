package ba.unsa.etf.nwt.userservice.auth;

import ba.unsa.etf.nwt.userservice.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email mora biti validan")
    private String email;

    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 8, message = "Lozinka mora imati najmanje 8 karaktera")
    private String password;

    @NotBlank(message = "Ime i prezime su obavezni")
    @Size(max = 100, message = "Ime ne smije biti duže od 100 karaktera")
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]{6,15}$", message = "Telefon mora biti validan")
    private String phone;

    private UserRole role;
}
