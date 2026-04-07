package ba.unsa.etf.nwt.portfolioservice.controller;

import ba.unsa.etf.nwt.portfolioservice.dto.*;
import ba.unsa.etf.nwt.portfolioservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.portfolioservice.model.PlanType;
import ba.unsa.etf.nwt.portfolioservice.model.SubscriptionStatus;
import ba.unsa.etf.nwt.portfolioservice.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean SubscriptionService subscriptionService;

    private PlanResponse basicPlanResponse() {
        PlanResponse p = new PlanResponse();
        p.setId(1L);
        p.setPlanType(PlanType.BASIC);
        p.setName("Basic");
        p.setPriceKm(BigDecimal.ZERO);
        p.setIsActive(true);
        return p;
    }

    private SubscriptionResponse sampleSubResponse() {
        SubscriptionResponse r = new SubscriptionResponse();
        r.setId(1L);
        r.setSalonId(1L);
        r.setPlan(basicPlanResponse());
        r.setStatus(SubscriptionStatus.ACTIVE);
        return r;
    }

    // ── SUCCESS ──────────────────────────────────────────────────────

    @Test
    void getPlans_returns200() throws Exception {
        when(subscriptionService.findAllPlans()).thenReturn(List.of(basicPlanResponse()));

        mockMvc.perform(get("/api/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planType").value("BASIC"));
    }

    @Test
    void getBySalon_existing_returns200() throws Exception {
        when(subscriptionService.findBySalonId(1L)).thenReturn(sampleSubResponse());

        mockMvc.perform(get("/api/subscriptions/salon/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void subscribe_validRequest_returns201() throws Exception {
        when(subscriptionService.subscribe(any())).thenReturn(sampleSubResponse());

        String body = """
            {
              "salonId": 1,
              "planType": "BASIC"
            }
            """;

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.salonId").value(1));
    }

    // ── FAILURE ──────────────────────────────────────────────────────

    @Test
    void getBySalon_unknown_returns404() throws Exception {
        when(subscriptionService.findBySalonId(99L))
                .thenThrow(new ResourceNotFoundException("Pretplata za salon ID=99 nije pronađena"));

        mockMvc.perform(get("/api/subscriptions/salon/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void subscribe_missingSalonId_returns400() throws Exception {
        String body = """
            {
              "planType": "BASIC"
            }
            """;

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.salonId").exists());
    }

    @Test
    void subscribe_missingPlanType_returns400() throws Exception {
        String body = """
            {
              "salonId": 1
            }
            """;

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.planType").exists());
    }
}
