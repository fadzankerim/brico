package com.nwt.userservice.service;

import com.nwt.userservice.dto.request.LoginRequest;
import com.nwt.userservice.dto.request.RegisterRequest;
import com.nwt.userservice.dto.response.AuthResponse;
import com.nwt.userservice.exception.BusinessException;
import com.nwt.userservice.exception.ResourceNotFoundException;
import com.nwt.userservice.model.User;
import com.nwt.userservice.repository.UserRepository;
import com.nwt.userservice.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS",
                    "User with email '" + request.getEmail() + "' already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user with email: {}", savedUser.getEmail());

        String accessToken = jwtTokenProvider.generateToken(savedUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.UserDto.builder()
                        .id(savedUser.getId())
                        .email(savedUser.getEmail())
                        .fullName(savedUser.getFullName())
                        .phone(savedUser.getPhone())
                        .role(savedUser.getRole())
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid email or password"));

        if (!user.isActive()) {
            throw new BusinessException("ACCOUNT_DISABLED", "This account has been disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid email or password");
        }

        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired");
        }

        Claims claims = jwtTokenProvider.getClaims(refreshToken);
        String tokenType = claims.get("type", String.class);
        if (!"refresh".equals(tokenType)) {
            throw new BusinessException("INVALID_TOKEN_TYPE", "Provided token is not a refresh token");
        }

        String userId = claims.getSubject();
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", Long.parseLong(userId)));

        if (!user.isActive()) {
            throw new BusinessException("ACCOUNT_DISABLED", "This account has been disabled");
        }

        String newAccessToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .build())
                .build();
    }
}
