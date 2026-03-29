package com.nwt.userservice;

import com.nwt.userservice.config.SecurityConfig;
import com.nwt.userservice.controller.UserController;
import com.nwt.userservice.dto.response.UserResponse;
import com.nwt.userservice.exception.GlobalExceptionHandler;
import com.nwt.userservice.exception.ResourceNotFoundException;
import com.nwt.userservice.model.UserRole;
import com.nwt.userservice.service.UserService;
import com.nwt.userservice.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.nwt.userservice.repository.UserRepository userRepository;

    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUserResponse = UserResponse.builder()
                .id(1L)
                .email("amela.begovic@gmail.com")
                .fullName("Amela Begović")
                .phone("+38763300001")
                .role(UserRole.CLIENT)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WhenUserExists_Returns200() throws Exception {
        when(userService.getById(1L)).thenReturn(testUserResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("amela.begovic@gmail.com"))
                .andExpect(jsonPath("$.fullName").value("Amela Begović"))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WhenUserDoesNotExist_Returns404() throws Exception {
        when(userService.getById(999L))
                .thenThrow(new ResourceNotFoundException("User", 999L));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }
}
