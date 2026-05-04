package ba.unsa.etf.nwt.bookingservice.controller;

import ba.unsa.etf.nwt.bookingservice.dto.AppointmentResponse;
import ba.unsa.etf.nwt.bookingservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.bookingservice.model.AppointmentStatus;
import ba.unsa.etf.nwt.bookingservice.grpc.EventGrpcClient;
import ba.unsa.etf.nwt.bookingservice.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AppointmentService appointmentService;
    @MockBean EventGrpcClient eventGrpcClient;

    private AppointmentResponse sampleResponse() {
        AppointmentResponse r = new AppointmentResponse();
        r.setId(1L);
        r.setClientId(1L);
        r.setClientName("Ana Anić");
        r.setSalonId(1L);
        r.setSalonName("Elite Cut");
        r.setStatus(AppointmentStatus.PENDING);
        r.setTotalPrice(new BigDecimal("15.00"));
        r.setStartTime(LocalDateTime.now().plusDays(1));
        r.setItems(List.of());
        return r;
    }

    // ── SUCCESS ──────────────────────────────────────────────────────

    @Test
    void getAll_returns200() throws Exception {
        when(appointmentService.findAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientName").value("Ana Anić"));
    }

    @Test
    void getById_existing_returns200() throws Exception {
        when(appointmentService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        when(appointmentService.create(any())).thenReturn(sampleResponse());

        String body = """
            {
              "clientId": 1,
              "clientName": "Ana Anić",
              "hairdresserId": 1,
              "hairdresserName": "Marko M.",
              "salonId": 1,
              "salonName": "Elite Cut",
              "startTime": "2026-06-01T10:00:00",
              "items": [
                {
                  "serviceId": 1,
                  "serviceName": "Šišanje",
                  "price": 15.00,
                  "durationMinutes": 30
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ── FAILURE ──────────────────────────────────────────────────────

    @Test
    void getById_unknown_returns404() throws Exception {
        when(appointmentService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Termin sa ID=99 nije pronađen"));

        mockMvc.perform(get("/api/appointments/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_emptyItems_returns400() throws Exception {
        String body = """
            {
              "clientId": 1,
              "clientName": "Ana",
              "hairdresserId": 1,
              "hairdresserName": "Marko",
              "salonId": 1,
              "salonName": "Elite Cut",
              "startTime": "2026-06-01T10:00:00",
              "items": []
            }
            """;

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void create_missingClientId_returns400() throws Exception {
        String body = """
            {
              "clientName": "Ana",
              "hairdresserId": 1,
              "hairdresserName": "Marko",
              "salonId": 1,
              "salonName": "Elite Cut",
              "startTime": "2026-06-01T10:00:00",
              "items": [{"serviceId":1,"serviceName":"Šišanje","price":15,"durationMinutes":30}]
            }
            """;

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.clientId").exists());
    }
}
