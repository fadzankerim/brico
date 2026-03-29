package com.nwt.appointmentservice.grpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for logging system events to the system-events-service.
 * Implemented as a plain HTTP client (RestTemplate) since the proto-generated classes
 * reside in system-events-service. Failures are swallowed and only logged.
 */
@Slf4j
@Component
public class SystemEventsClient {

    private final RestTemplate restTemplate;
    private final String systemEventsUrl;

    public SystemEventsClient(
            @Value("${system-events.service.url:http://system-events-service}") String systemEventsUrl) {
        this.restTemplate = new RestTemplate();
        this.systemEventsUrl = systemEventsUrl;
    }

    /**
     * Logs a system event to the system-events-service.
     *
     * @param microservice  name of the originating microservice
     * @param userId        the user ID associated with the action (can be null)
     * @param actionType    type of action (e.g. "APPOINTMENT_CREATED", "REVIEW_POSTED")
     * @param resourceName  the resource being acted upon (e.g. "Appointment", "Review")
     * @param responseType  outcome of the action (e.g. "SUCCESS", "FAILURE")
     */
    public void logEvent(String microservice,
                         Long userId,
                         String actionType,
                         String resourceName,
                         String responseType) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("microservice", microservice);
            payload.put("userId", userId);
            payload.put("actionType", actionType);
            payload.put("resourceName", resourceName);
            payload.put("responseType", responseType);
            payload.put("timestamp", LocalDateTime.now().toString());

            String url = systemEventsUrl + "/api/events/log";
            restTemplate.postForEntity(url, payload, Void.class);
            log.debug("System event logged: action={}, resource={}, userId={}", actionType, resourceName, userId);
        } catch (Exception e) {
            // Failures must never propagate — event logging is best-effort
            log.warn("Failed to log system event (action={}, resource={}): {}", actionType, resourceName, e.getMessage());
        }
    }
}
