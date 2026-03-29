package com.nwt.systemeventsservice.grpc;

import com.nwt.systemeventsservice.grpc.proto.*;
import com.nwt.systemeventsservice.model.SystemEvent;
import com.nwt.systemeventsservice.service.SystemEventService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemEventsGrpcServer extends SystemEventsServiceGrpc.SystemEventsServiceImplBase {

    private final SystemEventService systemEventService;

    @Override
    public void logEvent(LogEventRequest request, StreamObserver<LogEventResponse> responseObserver) {
        log.info("gRPC logEvent called: microservice={}, userId={}, action={}",
                request.getMicroservice(), request.getUserId(), request.getActionType());

        try {
            Instant timestamp;
            try {
                timestamp = Instant.parse(request.getTimestamp());
            } catch (DateTimeParseException e) {
                timestamp = Instant.now();
            }

            SystemEvent event = SystemEvent.builder()
                    .microservice(request.getMicroservice())
                    .userId(request.getUserId())
                    .actionType(request.getActionType().name())
                    .resourceName(request.getResourceName())
                    .responseType(request.getResponseType().name())
                    .timestamp(timestamp)
                    .detail(request.getDetail())
                    .build();

            SystemEvent saved = systemEventService.save(event);

            LogEventResponse response = LogEventResponse.newBuilder()
                    .setSuccess(true)
                    .setEventId(saved.getId())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error saving event via gRPC", e);
            LogEventResponse errorResponse = LogEventResponse.newBuilder()
                    .setSuccess(false)
                    .setEventId("")
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getEvents(GetEventsRequest request, StreamObserver<GetEventsResponse> responseObserver) {
        log.info("gRPC getEvents called: microservice={}, userId={}, page={}, size={}",
                request.getMicroservice(), request.getUserId(), request.getPage(), request.getSize());

        try {
            int page = Math.max(0, request.getPage());
            int size = request.getSize() > 0 ? request.getSize() : 20;

            Page<SystemEvent> eventsPage = systemEventService.findByFilters(
                    request.getMicroservice(),
                    request.getUserId(),
                    null,
                    PageRequest.of(page, size)
            );

            List<EventRecord> eventRecords = eventsPage.getContent().stream()
                    .map(this::toEventRecord)
                    .toList();

            GetEventsResponse response = GetEventsResponse.newBuilder()
                    .addAllEvents(eventRecords)
                    .setTotalCount(eventsPage.getTotalElements())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error retrieving events via gRPC", e);
            responseObserver.onError(e);
        }
    }

    private EventRecord toEventRecord(SystemEvent event) {
        ActionType actionType;
        try {
            actionType = ActionType.valueOf(event.getActionType());
        } catch (IllegalArgumentException e) {
            actionType = ActionType.GET;
        }

        ResponseType responseType;
        try {
            responseType = ResponseType.valueOf(event.getResponseType());
        } catch (IllegalArgumentException e) {
            responseType = ResponseType.SUCCESS;
        }

        return EventRecord.newBuilder()
                .setEventId(event.getId() != null ? event.getId() : "")
                .setMicroservice(event.getMicroservice() != null ? event.getMicroservice() : "")
                .setUserId(event.getUserId() != null ? event.getUserId() : "")
                .setActionType(actionType)
                .setResourceName(event.getResourceName() != null ? event.getResourceName() : "")
                .setResponseType(responseType)
                .setTimestamp(event.getTimestamp() != null ? event.getTimestamp().toString() : "")
                .setDetail(event.getDetail() != null ? event.getDetail() : "")
                .build();
    }
}
