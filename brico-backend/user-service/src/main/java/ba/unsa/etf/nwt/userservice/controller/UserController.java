package ba.unsa.etf.nwt.userservice.controller;

import ba.unsa.etf.nwt.userservice.dto.UpdateUserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserResponse;
import ba.unsa.etf.nwt.userservice.model.UserRole;
import ba.unsa.etf.nwt.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Upravljanje korisnicima")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Dohvati sve korisnike", description = "Opcionalno filtriraj po roli (?role=CLIENT)")
    public ResponseEntity<List<UserResponse>> getAll(
            @RequestParam(required = false) UserRole role) {
        List<UserResponse> users = (role != null)
                ? userService.findByRole(role)
                : userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dohvati korisnika po ID-u")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Dohvati korisnika po emailu")
    public ResponseEntity<UserResponse> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novog korisnika")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(req));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Ažuriraj podatke korisnika")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(userService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obriši korisnika")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
