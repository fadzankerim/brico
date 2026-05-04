package ba.unsa.etf.nwt.bookingservice.grpc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventInterceptor implements HandlerInterceptor {

    private final EventGrpcClient eventGrpcClient;

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/actuator") || uri.startsWith("/swagger") || uri.startsWith("/api-docs")) {
            return;
        }

        String actionType   = mapMethod(request.getMethod());
        String resourceName = extractResource(uri);
        String responseType = response.getStatus() < 400 ? "SUCCESS" : "ERROR";

        eventGrpcClient.logEvent(actionType, resourceName, responseType, null,
                request.getMethod() + " " + uri + " → " + response.getStatus());
    }

    private String mapMethod(String method) {
        return switch (method.toUpperCase()) {
            case "POST"         -> "CREATE";
            case "DELETE"       -> "DELETE";
            case "PUT", "PATCH" -> "UPDATE";
            default             -> "GET";
        };
    }

    private String extractResource(String uri) {
        if (uri.contains("/appointments")) return "Appointment";
        return "Unknown";
    }
}
