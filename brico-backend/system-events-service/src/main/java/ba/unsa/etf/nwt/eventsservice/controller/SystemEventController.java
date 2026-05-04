package ba.unsa.etf.nwt.eventsservice.controller;

import ba.unsa.etf.nwt.eventsservice.dto.SystemEventResponse;
import ba.unsa.etf.nwt.eventsservice.repository.SystemEventRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "System Events", description = "Audit log svih sistemskih akcija")
public class SystemEventController {

    private final SystemEventRepository eventRepository;

    @GetMapping
    @Operation(summary = "Svi eventi sa paginacijom", description = "Filtriraj po servisu: ?service=booking-service")
    public ResponseEntity<Page<SystemEventResponse>> getEvents(
            @RequestParam(required = false) String service,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<SystemEventResponse> page = (service != null && !service.isBlank())
                ? eventRepository.findByServiceNameOrderByTimestampDesc(service, pageable).map(SystemEventResponse::from)
                : eventRepository.findAllByOrderByTimestampDesc(pageable).map(SystemEventResponse::from);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/recent")
    @Operation(summary = "Poslednjih 50 evenata")
    public ResponseEntity<List<SystemEventResponse>> getRecent(
            @RequestParam(required = false) String service) {

        List<SystemEventResponse> events = (service != null && !service.isBlank())
                ? eventRepository.findTop50ByServiceNameOrderByTimestampDesc(service)
                        .stream().map(SystemEventResponse::from).toList()
                : eventRepository.findTop50ByOrderByTimestampDesc()
                        .stream().map(SystemEventResponse::from).toList();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/stats")
    @Operation(summary = "Broj evenata po servisu")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = eventRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getServiceName(),
                        Collectors.counting()));
        return ResponseEntity.ok(stats);
    }
}
