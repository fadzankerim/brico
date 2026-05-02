package ba.unsa.etf.nwt.bookingservice.grpc;

import ba.unsa.etf.nwt.eventsservice.proto.EventRequest;
import ba.unsa.etf.nwt.eventsservice.proto.EventServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class EventGrpcClient {

    @GrpcClient("system-events-service")
    private EventServiceGrpc.EventServiceBlockingStub eventStub;

    public void logEvent(String actionType, String resourceName,
                         String responseType, String userId, String details) {
        try {
            EventRequest request = EventRequest.newBuilder()
                    .setServiceName("booking-service")
                    .setActionType(actionType)
                    .setResourceName(resourceName != null ? resourceName : "")
                    .setResponseType(responseType)
                    .setTimestamp(LocalDateTime.now().toString())
                    .setUserId(userId != null ? userId : "")
                    .setDetails(details != null ? details : "")
                    .build();
            eventStub.logEvent(request);
        } catch (StatusRuntimeException e) {
            log.warn("system-events-service nije dostupan: {}", e.getStatus());
        } catch (Exception e) {
            log.warn("Greška pri logovanju eventa: {}", e.getMessage());
        }
    }
}
