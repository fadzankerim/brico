package ba.unsa.etf.nwt.userservice.controller;

import ba.unsa.etf.nwt.userservice.dto.UpdateCredentialsRequest;
import ba.unsa.etf.nwt.userservice.dto.UpdateUserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserResponse;
import ba.unsa.etf.nwt.userservice.model.UserRole;
import ba.unsa.etf.nwt.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // ── Paginacija + sortiranje ────────────────────────────────────────

    @GetMapping("/paged")
    @Operation(summary = "Dohvati korisnike sa paginacijom",
               description = "Podržava ?page=0&size=10&sort=createdAt,desc i filtriranje po roli")
    public ResponseEntity<Page<UserResponse>> getAllPaged(
            @RequestParam(required = false) UserRole role,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserResponse> page = (role != null)
                ? userService.findByRolePaged(role, pageable)
                : userService.findAllPaged(pageable);
        return ResponseEntity.ok(page);
    }

    // ── Custom pretraga ────────────────────────────────────────────────

    @GetMapping("/search")
    @Operation(summary = "Pretraži korisnike po imenu ili emailu")
    public ResponseEntity<Page<UserResponse>> search(
            @RequestParam String q,
            @PageableDefault(size = 10, sort = "fullName") Pageable pageable) {
        return ResponseEntity.ok(userService.search(q, pageable));
    }

    // ── Statistika ────────────────────────────────────────────────────

    @GetMapping("/stats")
    @Operation(summary = "Broj korisnika po roli")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(userService.getStats());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dohvati korisnika po ID-u")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Interni endpoint za inter-service validaciju.
     * Koristi ga booking-service da provjeri da li klijent postoji i aktivan je.
     */
    @GetMapping("/{id}/validate")
    @Operation(summary = "[Internal] Validacija korisnika za inter-service komunikaciju",
               description = "Vraća 200 ako korisnik postoji i aktivan je, 404 ako ne postoji, 409 ako nije aktivan")
    public ResponseEntity<Map<String, Object>> validate(@PathVariable Long id) {
        return ResponseEntity.ok(userService.validate(id));
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
    @Operation(summary = "Ažuriraj podatke korisnika (parcijalni update)")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(userService.update(id, req));
    }

    @PatchMapping("/{id}/credentials")
    @Operation(summary = "Ažuriraj email i/ili lozinku korisnika")
    public ResponseEntity<UserResponse> updateCredentials(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateCredentialsRequest req) {
        return ResponseEntity.ok(userService.updateCredentials(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obriši korisnika")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
