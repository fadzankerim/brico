package com.nwt.notificationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nwt.notificationservice.controller.NotificationController;
import com.nwt.notificationservice.dto.response.NotificationResponse;
import com.nwt.notificationservice.model.NotificationStatus;
import com.nwt.notificationservice.model.NotificationType;
import com.nwt.notificationservice.service.NotificationService;
import com.nwt.notificationservice.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @Test
    @DisplayName("GET /api/notifications/user/{userId} returns paginated notifications")
    void getNotificationsByUser_returnsPaginatedResults() throws Exception {
        Long userId = 1L;

        NotificationResponse response1 = NotificationResponse.builder()
                .id(10L)
                .userId(userId)
                .appointmentId(100L)
                .type(NotificationType.APPOINTMENT_BOOKED)
                .status(NotificationStatus.PENDING)
                .message("Your appointment has been booked.")
                .createdAt(LocalDateTime.now())
                .build();

        NotificationResponse response2 = NotificationResponse.builder()
                .id(11L)
                .userId(userId)
                .appointmentId(101L)
                .type(NotificationType.APPOINTMENT_REMINDER_24H)
                .status(NotificationStatus.SENT)
                .message("Reminder: appointment tomorrow.")
                .sentAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        PageImpl<NotificationResponse> page =
                new PageImpl<>(List.of(response1, response2), PageRequest.of(0, 20), 2);

        when(notificationService.getNotificationsByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/notifications/user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].userId").value(userId))
                .andExpect(jsonPath("$.content[0].type").value("APPOINTMENT_BOOKED"))
                .andExpect(jsonPath("$.content[1].id").value(11))
                .andExpect(jsonPath("$.content[1].status").value("SENT"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/notifications/user/{userId} returns empty page when no notifications")
    void getNotificationsByUser_returnsEmptyPage() throws Exception {
        Long userId = 99L;

        PageImpl<NotificationResponse> emptyPage =
                new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

        when(notificationService.getNotificationsByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/notifications/user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/notifications/user/{userId} supports pagination parameters")
    void getNotificationsByUser_supportsPaginationParams() throws Exception {
        Long userId = 2L;

        PageImpl<NotificationResponse> page =
                new PageImpl<>(List.of(), PageRequest.of(1, 5), 0);

        when(notificationService.getNotificationsByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/notifications/user/{userId}", userId)
                        .param("page", "1")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
