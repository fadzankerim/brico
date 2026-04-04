package ba.unsa.etf.nwt.userservice.service;

import ba.unsa.etf.nwt.userservice.dto.UpdateUserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserResponse;
import ba.unsa.etf.nwt.userservice.exception.DuplicateResourceException;
import ba.unsa.etf.nwt.userservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.userservice.model.User;
import ba.unsa.etf.nwt.userservice.model.UserRole;
import ba.unsa.etf.nwt.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks UserService userService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setup() {
        // Inject real ModelMapper into service via reflection
        try {
            var field = UserService.class.getDeclaredField("modelMapper");
            field.setAccessible(true);
            field.set(userService, modelMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User sampleUser() {
        return User.builder()
                .id(1L)
                .email("test@brico.ba")
                .password("password123")
                .fullName("Test Korisnik")
                .role(UserRole.CLIENT)
                .emailVerified(false)
                .build();
    }

    @Test
    void findAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser()));
        List<UserResponse> result = userService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@brico.ba");
    }

    @Test
    void findById_existingId_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser()));
        UserResponse resp = userService.findById(1L);
        assertThat(resp.getFullName()).isEqualTo("Test Korisnik");
    }

    @Test
    void findById_unknownId_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_newEmail_savesAndReturns() {
        when(userRepository.existsByEmail("new@brico.ba")).thenReturn(false);
        User saved = sampleUser();
        saved.setEmail("new@brico.ba");
        when(userRepository.save(any())).thenReturn(saved);

        UserRequest req = new UserRequest();
        req.setEmail("new@brico.ba");
        req.setPassword("password123");
        req.setFullName("Novi Korisnik");

        UserResponse resp = userService.create(req);
        assertThat(resp.getEmail()).isEqualTo("new@brico.ba");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_duplicateEmail_throwsConflict() {
        when(userRepository.existsByEmail("test@brico.ba")).thenReturn(true);

        UserRequest req = new UserRequest();
        req.setEmail("test@brico.ba");
        req.setPassword("password123");
        req.setFullName("Dupli");

        assertThatThrownBy(() -> userService.create(req))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_existingUser_updatesFields() {
        User user = sampleUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserRequest req = new UpdateUserRequest();
        req.setFullName("Novo Ime");
        req.setPhone("+38761000000");

        UserResponse resp = userService.update(1L, req);
        assertThat(resp.getFullName()).isEqualTo("Novo Ime");
        assertThat(resp.getPhone()).isEqualTo("+38761000000");
    }

    @Test
    void delete_existingUser_deletesSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser()));
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_unknownUser_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
