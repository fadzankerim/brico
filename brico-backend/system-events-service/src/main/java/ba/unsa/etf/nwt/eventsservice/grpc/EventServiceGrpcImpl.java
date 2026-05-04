package ba.unsa.etf.nwt.eventsservice.grpc;

import ba.unsa.etf.nwt.eventsservice.model.SystemEvent;
import ba.unsa.etf.nwt.eventsservice.proto.EventRequest;
import ba.unsa.etf.nwt.eventsservice.proto.EventResponse;
import ba.unsa.etf.nwt.eventsservice.proto.EventServiceGrpc;
import ba.unsa.etf.nwt.eventsservice.proto.GetEventsRequest;
import ba.unsa.etf.nwt.eventsservice.proto.GetEventsResponse;
import ba.unsa.etf.nwt.eventsservice.repository.SystemEventRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class EventServiceGrpcImpl extends EventServiceGrpc.EventServiceImplBase {

    private final SystemEventRepository eventRepository;

    @Override
    public void logEvent(EventRequest request, StreamObserver<EventResponse> responseObserver) {
        try {
            SystemEvent.ActionType actionType;
            try {
                actionType = SystemEvent.ActionType.valueOf(request.getActionType().toUpperCase());
            } catch (IllegalArgumentException e) {
                actionType = SystemEvent.ActionType.GET;
            }

            SystemEvent.ResponseType responseType;
            try {
                responseType = SystemEvent.ResponseType.valueOf(request.getResponseType().toUpperCase());
            } catch (IllegalArgumentException e) {
                responseType = SystemEvent.ResponseType.SUCCESS;
            }

            SystemEvent event = SystemEvent.builder()
                    .serviceName(request.getServiceName())
                    .actionType(actionType)
                    .resourceName(request.getResourceName())
                    .responseType(responseType)
                    .timestamp(request.getTimestamp().isBlank()
                            ? LocalDateTime.now()
                            : LocalDateTime.parse(request.getTimestamp()))
                    .userId(request.getUserId().isBlank() ? null : request.getUserId())
                    .details(request.getDetails().isBlank() ? null : request.getDetails())
                    .build();

            SystemEvent saved = eventRepository.save(event);
            log.debug("Event logovan: {} {} {}", saved.getServiceName(), saved.getActionType(), saved.getResponseType());

            responseObserver.onNext(EventResponse.newBuilder()
                    .setId(saved.getId())
                    .setSuccess(true)
                    .setMessage("Event logovan")
                    .build());
        } catch (Exception e) {
            log.error("Greška pri logovanju eventa: {}", e.getMessage());
            responseObserver.onNext(EventResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getRecentEvents(GetEventsRequest request, StreamObserver<GetEventsResponse> responseObserver) {
        List<SystemEvent> events = request.getServiceName().isBlank()
                ? eventRepository.findTop50ByOrderByTimestampDesc()
                : eventRepository.findTop50ByServiceNameOrderByTimestampDesc(request.getServiceName());

        GetEventsResponse.Builder builder = GetEventsResponse.newBuilder();
        for (SystemEvent e : events) {
            builder.addEvents(EventRequest.newBuilder()
                    .setServiceName(e.getServiceName())
                    .setActionType(e.getActionType().name())
                    .setResourceName(e.getResourceName() != null ? e.getResourceName() : "")
                    .setResponseType(e.getResponseType().name())
                    .setTimestamp(e.getTimestamp().toString())
                    .setUserId(e.getUserId() != null ? e.getUserId() : "")
                    .setDetails(e.getDetails() != null ? e.getDetails() : "")
                    .build());
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
