package ba.unsa.etf.nwt.salonservice.controller;

import ba.unsa.etf.nwt.salonservice.dto.SalonResponse;
import ba.unsa.etf.nwt.salonservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.salonservice.service.SalonMgmtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalonController.class)
class SalonControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean SalonMgmtService salonMgmtService;

    private SalonResponse sampleResponse() {
        SalonResponse r = new SalonResponse();
        r.setId(1L);
        r.setName("Elite Cut");
        r.setSlug("elite-cut");
        r.setCity("Sarajevo");
        r.setAddress("Titova 1");
        r.setOwnerId(1L);
        r.setVerified(false);
        r.setIsActive(true);
        return r;
    }

    // ── SUCCESS ──────────────────────────────────────────────────────

    @Test
    void getAll_returns200() throws Exception {
        when(salonMgmtService.findAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/salons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Elite Cut"));
    }

    @Test
    void getById_existing_returns200() throws Exception {
        when(salonMgmtService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/salons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("elite-cut"));
    }

    @Test
    void createSalon_validRequest_returns201() throws Exception {
        when(salonMgmtService.create(any())).thenReturn(sampleResponse());

        String body = """
            {
              "name": "Elite Cut",
              "city": "Sarajevo",
              "address": "Titova 1",
              "ownerId": 1
            }
            """;

        mockMvc.perform(post("/api/salons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ── FAILURE ──────────────────────────────────────────────────────

    @Test
    void getById_unknown_returns404() throws Exception {
        when(salonMgmtService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Salon sa ID=99 nije pronađen"));

        mockMvc.perform(get("/api/salons/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void createSalon_missingCity_returns400() throws Exception {
        String body = """
            {
              "name": "Elite Cut",
              "address": "Titova 1",
              "ownerId": 1
            }
            """;

        mockMvc.perform(post("/api/salons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"))
                .andExpect(jsonPath("$.fields.city").exists());
    }

    @Test
    void createSalon_missingOwnerId_returns400() throws Exception {
        String body = """
            {
              "name": "Elite Cut",
              "city": "Sarajevo",
              "address": "Titova 1"
            }
            """;

        mockMvc.perform(post("/api/salons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.ownerId").exists());
    }
}
