package ba.unsa.etf.nwt.userservice.controller;

import ba.unsa.etf.nwt.userservice.dto.UserResponse;
import ba.unsa.etf.nwt.userservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.userservice.model.UserRole;
import ba.unsa.etf.nwt.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserService userService;

    private UserResponse sampleResponse() {
        UserResponse r = new UserResponse();
        r.setId(1L);
        r.setEmail("test@brico.ba");
        r.setFullName("Test Korisnik");
        r.setRole(UserRole.CLIENT);
        r.setEmailVerified(false);
        return r;
    }

    // ── SUCCESS cases ────────────────────────────────────────────────

    @Test
    void getAll_returns200WithList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@brico.ba"));
    }

    @Test
    void getById_existingUser_returns200() throws Exception {
        when(userService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Test Korisnik"));
    }

    @Test
    void createUser_validRequest_returns201() throws Exception {
        when(userService.create(any())).thenReturn(sampleResponse());

        String body = """
            {
              "email": "test@brico.ba",
              "password": "password123",
              "fullName": "Test Korisnik"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ── FAILURE cases ────────────────────────────────────────────────

    @Test
    void getById_unknownId_returns404() throws Exception {
        when(userService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Korisnik sa ID=99 nije pronađen"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void createUser_missingEmail_returns400WithValidationError() throws Exception {
        String body = """
            {
              "password": "password123",
              "fullName": "Test"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"))
                .andExpect(jsonPath("$.fields.email").exists());
    }

    @Test
    void createUser_invalidEmail_returns400() throws Exception {
        String body = """
            {
              "email": "not-an-email",
              "password": "password123",
              "fullName": "Test"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void createUser_shortPassword_returns400() throws Exception {
        String body = """
            {
              "email": "test@brico.ba",
              "password": "short",
              "fullName": "Test"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    // ── Novi testovi za Zadatak 4 ─────────────────────────────────────

    @Test
    void getAllPaged_returns200WithPageObject() throws Exception {
        when(userService.findAllPaged(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse())));

        mockMvc.perform(get("/api/users/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test@brico.ba"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void search_returns200WithMatchingUsers() throws Exception {
        when(userService.search(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse())));

        mockMvc.perform(get("/api/users/search").param("q", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Test Korisnik"));
    }

    @Test
    void getStats_returns200WithMap() throws Exception {
        when(userService.getStats()).thenReturn(Map.of("CLIENT", 5L, "OWNER", 2L));

        mockMvc.perform(get("/api/users/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CLIENT").value(5));
    }
}
