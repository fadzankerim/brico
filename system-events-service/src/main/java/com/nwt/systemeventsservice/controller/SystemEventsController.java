package com.nwt.systemeventsservice.controller;

import com.nwt.systemeventsservice.model.SystemEvent;
import com.nwt.systemeventsservice.service.SystemEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class SystemEventsController {

    private final SystemEventService systemEventService;

    /**
     * GET /api/events?microservice=&userId=&actionType=&page=0&size=20
     * Returns paginated list of system events, optionally filtered.
     */
    @GetMapping
    public ResponseEntity<Page<SystemEvent>> getEvents(
            @RequestParam(required = false) String microservice,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String actionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("GET /api/events - microservice={}, userId={}, actionType={}, page={}, size={}",
                microservice, userId, actionType, page, size);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<SystemEvent> events = systemEventService.findByFilters(microservice, userId, actionType, pageRequest);
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/events/{id}
     * Returns a single system event by its MongoDB document ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SystemEvent> getEventById(@PathVariable String id) {
        log.debug("GET /api/events/{}", id);
        return systemEventService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/events/{id}
     * Deletes a system event (admin use).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        log.debug("DELETE /api/events/{}", id);
        if (systemEventService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        systemEventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
