package ba.unsa.etf.nwt.reviewservice.controller;

import ba.unsa.etf.nwt.reviewservice.dto.ReviewResponse;
import ba.unsa.etf.nwt.reviewservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.reviewservice.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ReviewService reviewService;

    private ReviewResponse sampleResponse() {
        ReviewResponse r = new ReviewResponse();
        r.setId(1L);
        r.setClientId(1L);
        r.setClientName("Ana Anić");
        r.setSalonId(1L);
        r.setRating(5);
        r.setComment("Odlično!");
        return r;
    }

    // ── SUCCESS ──────────────────────────────────────────────────────

    @Test
    void getBySalon_returns200() throws Exception {
        when(reviewService.findBySalonId(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/salons/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void getSalonRating_returns200() throws Exception {
        when(reviewService.getSalonRating(1L))
                .thenReturn(Map.of("salonId", 1L, "averageRating", 4.5, "reviewCount", 10L));

        mockMvc.perform(get("/api/salons/1/rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5));
    }

    @Test
    void createReview_validRequest_returns201() throws Exception {
        when(reviewService.create(any())).thenReturn(sampleResponse());

        String body = """
            {
              "clientId": 1,
              "clientName": "Ana Anić",
              "salonId": 1,
              "rating": 5,
              "comment": "Odlično!"
            }
            """;

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value("Odlično!"));
    }

    // ── FAILURE ──────────────────────────────────────────────────────

    @Test
    void getById_unknown_returns404() throws Exception {
        when(reviewService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Recenzija sa ID=99 nije pronađena"));

        mockMvc.perform(get("/api/reviews/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void createReview_invalidRating_returns400() throws Exception {
        String body = """
            {
              "clientId": 1,
              "clientName": "Ana",
              "salonId": 1,
              "rating": 10
            }
            """;

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.rating").exists());
    }

    @Test
    void createReview_missingSalonId_returns400() throws Exception {
        String body = """
            {
              "clientId": 1,
              "clientName": "Ana",
              "rating": 4
            }
            """;

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.salonId").exists());
    }
}
