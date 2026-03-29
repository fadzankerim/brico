package com.nwt.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nwt.userservice.config.SecurityConfig;
import com.nwt.userservice.controller.AuthController;
import com.nwt.userservice.dto.request.RegisterRequest;
import com.nwt.userservice.dto.response.AuthResponse;
import com.nwt.userservice.exception.BusinessException;
import com.nwt.userservice.exception.GlobalExceptionHandler;
import com.nwt.userservice.model.UserRole;
import com.nwt.userservice.service.AuthService;
import com.nwt.userservice.util.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.nwt.userservice.repository.UserRepository userRepository;

    @Test
    void register_WhenValidRequest_Returns201() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("nermin.muratovic@gmail.com")
                .password("Password123!")
                .fullName("Nermin Muratović")
                .phone("+38761300002")
                .role(UserRole.CLIENT)
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access.jwt.token")
                .refreshToken("refresh.jwt.token")
                .user(AuthResponse.UserDto.builder()
                        .id(1L)
                        .email("nermin.muratovic@gmail.com")
                        .fullName("Nermin Muratović")
                        .phone("+38761300002")
                        .role(UserRole.CLIENT)
                        .build())
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access.jwt.token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh.jwt.token"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("nermin.muratovic@gmail.com"))
                .andExpect(jsonPath("$.user.role").value("CLIENT"));
    }

    @Test
    void register_WhenEmailAlreadyExists_Returns400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("existing@gmail.com")
                .password("Password123!")
                .fullName("Damir Hadžić")
                .phone("+38763300004")
                .role(UserRole.CLIENT)
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException("EMAIL_ALREADY_EXISTS",
                        "User with email 'existing@gmail.com' already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("User with email 'existing@gmail.com' already exists"));
    }
}
