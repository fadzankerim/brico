package com.nwt.userservice;

import com.nwt.userservice.dto.response.UserResponse;
import com.nwt.userservice.exception.ResourceNotFoundException;
import com.nwt.userservice.model.User;
import com.nwt.userservice.model.UserRole;
import com.nwt.userservice.repository.UserRepository;
import com.nwt.userservice.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("amela.begovic@gmail.com")
                .passwordHash("$2a$10$hashedpassword")
                .fullName("Amela Begović")
                .phone("+38763300001")
                .role(UserRole.CLIENT)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getById_WhenUserExists_ReturnsUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("amela.begovic@gmail.com");
        assertThat(result.getFullName()).isEqualTo("Amela Begović");
        assertThat(result.getPhone()).isEqualTo("+38763300001");
        assertThat(result.getRole()).isEqualTo(UserRole.CLIENT);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void getById_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
