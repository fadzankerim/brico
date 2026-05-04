package ba.unsa.etf.nwt.userservice.auth;

import ba.unsa.etf.nwt.userservice.dto.UserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserResponse;
import ba.unsa.etf.nwt.userservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.userservice.model.User;
import ba.unsa.etf.nwt.userservice.repository.UserRepository;
import ba.unsa.etf.nwt.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Prijava, refresh i odjava")
public class AuthController {

    private final UserRepository          userRepository;
    private final RefreshTokenRepository  refreshTokenRepository;
    private final JwtService              jwtService;
    private final PasswordEncoder         passwordEncoder;
    private final ModelMapper             modelMapper;
    private final UserService             userService;

    @PostMapping("/register")
    @Operation(summary = "Registracija — kreira korisnika i vraća tokene")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequest req) {
        UserResponse created = userService.create(req);
        User user = userRepository.findById(created.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        String accessToken       = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken();

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(900L)
                .user(created)
                .build());
    }

    @GetMapping("/me")
    @Operation(summary = "Dohvati trenutno prijavljenog korisnika iz tokena")
    public ResponseEntity<UserResponse> me(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new ResourceNotFoundException("Korisnik nije autentificiran");
        }
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PostMapping("/login")
    @Operation(summary = "Prijava — vraća access token (15 min) i refresh token (7 dana)")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Pogrešan email ili lozinka"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Pogrešan email ili lozinka");
        }
        if (!user.getIsActive()) {
            throw new IllegalStateException("Nalog je deaktiviran");
        }

        String accessToken        = jwtService.generateAccessToken(user);
        String refreshTokenValue  = jwtService.generateRefreshToken();

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(900L)
                .user(modelMapper.map(user, UserResponse.class))
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Osvježi access token pomoću refresh tokena")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        RefreshToken rt = refreshTokenRepository.findByToken(req.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token nije validan"));

        if (rt.isExpired()) {
            refreshTokenRepository.delete(rt);
            throw new IllegalStateException("Refresh token je istekao, prijavite se ponovo");
        }

        User user = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik ne postoji"));

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(req.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(900L)
                .user(modelMapper.map(user, UserResponse.class))
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Odjava — briše refresh token iz baze")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId != null) {
            refreshTokenRepository.deleteByUserId(userId);
        }
        return ResponseEntity.noContent().build();
    }
}
