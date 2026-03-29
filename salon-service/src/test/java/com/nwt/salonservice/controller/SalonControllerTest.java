package com.nwt.salonservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nwt.salonservice.dto.request.SalonRequest;
import com.nwt.salonservice.dto.response.SalonResponse;
import com.nwt.salonservice.dto.response.SalonSummaryResponse;
import com.nwt.salonservice.exception.GlobalExceptionHandler;
import com.nwt.salonservice.exception.ResourceNotFoundException;
import com.nwt.salonservice.service.SalonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SalonControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SalonService salonService;

    @InjectMocks
    private SalonController salonController;

    private SalonResponse testSalonResponse;
    private SalonRequest validRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(salonController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        testSalonResponse = SalonResponse.builder()
                .id(1L)
                .ownerId(10L)
                .name("Glam Studio")
                .city("Sarajevo")
                .avgRating(4.8)
                .verified(true)
                .build();

        validRequest = SalonRequest.builder()
                .ownerId(10L)
                .name("Glam Studio")
                .city("Sarajevo")
                .build();
    }

    @Test
    void getSalonById_WhenExists_Returns200() throws Exception {
        when(salonService.findById(1L)).thenReturn(testSalonResponse);

        mockMvc.perform(get("/api/salons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Glam Studio"))
                .andExpect(jsonPath("$.city").value("Sarajevo"));
    }

    @Test
    void getSalonById_WhenNotFound_Returns404() throws Exception {
        when(salonService.findById(999L)).thenThrow(new ResourceNotFoundException("Salon", 999L));

        mockMvc.perform(get("/api/salons/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void createSalon_WithValidRequest_Returns201() throws Exception {
        when(salonService.create(any(SalonRequest.class))).thenReturn(testSalonResponse);

        mockMvc.perform(post("/api/salons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .header("X-User-Role", "SALON_OWNER"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Glam Studio"));
    }

    @Test
    void createSalon_WithoutRole_Returns403() throws Exception {
        mockMvc.perform(post("/api/salons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .header("X-User-Role", "CLIENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSalon_WithMissingName_Returns400() throws Exception {
        SalonRequest invalidRequest = SalonRequest.builder()
                .ownerId(10L)
                .city("Sarajevo")
                .build();

        mockMvc.perform(post("/api/salons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .header("X-User-Role", "SALON_OWNER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void searchSalons_ReturnsPagedResults() throws Exception {
        SalonSummaryResponse summary = SalonSummaryResponse.builder()
                .id(1L)
                .name("Glam Studio")
                .city("Sarajevo")
                .avgRating(4.8)
                .build();
        Page<SalonSummaryResponse> page = new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1);
        when(salonService.searchSalons(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/salons").param("city", "Sarajevo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Glam Studio"));
    }

    @Test
    void verifySalon_AsAdmin_Returns200() throws Exception {
        testSalonResponse.setVerified(true);
        when(salonService.verify(1L)).thenReturn(testSalonResponse);

        mockMvc.perform(patch("/api/salons/1/verify")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));
    }

    @Test
    void verifySalon_AsNonAdmin_Returns403() throws Exception {
        mockMvc.perform(patch("/api/salons/1/verify")
                        .header("X-User-Role", "SALON_OWNER"))
                .andExpect(status().isForbidden());
    }
}
