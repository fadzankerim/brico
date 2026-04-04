package ba.unsa.etf.nwt.bookingservice.service;

import ba.unsa.etf.nwt.bookingservice.dto.*;
import ba.unsa.etf.nwt.bookingservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.bookingservice.model.*;
import ba.unsa.etf.nwt.bookingservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    public List<AppointmentResponse> findBySalonId(Long salonId) {
        return appointmentRepository.findBySalonId(salonId).stream()
                .map(this::toResponse).toList();
    }

    public List<AppointmentResponse> findByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(this::toResponse).toList();
    }

    public List<AppointmentResponse> findByHairdresserId(Long hairdresserId) {
        return appointmentRepository.findByHairdresserId(hairdresserId).stream()
                .map(this::toResponse).toList();
    }

    public AppointmentResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest req) {
        // Calculate total price and end time from items
        BigDecimal total = req.getItems().stream()
                .map(AppointmentItemRequest::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalMinutes = req.getItems().stream()
                .mapToInt(AppointmentItemRequest::getDurationMinutes).sum();

        Appointment appt = Appointment.builder()
                .clientId(req.getClientId())
                .clientName(req.getClientName())
                .clientPhone(req.getClientPhone())
                .hairdresserId(req.getHairdresserId())
                .hairdresserName(req.getHairdresserName())
                .salonId(req.getSalonId())
                .salonName(req.getSalonName())
                .salonAddress(req.getSalonAddress())
                .startTime(req.getStartTime())
                .endTime(req.getStartTime().plusMinutes(totalMinutes))
                .totalPrice(total)
                .notes(req.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();

        for (AppointmentItemRequest item : req.getItems()) {
            AppointmentItem ai = AppointmentItem.builder()
                    .serviceId(item.getServiceId())
                    .serviceName(item.getServiceName())
                    .price(item.getPrice())
                    .durationMinutes(item.getDurationMinutes())
                    .appointment(appt)
                    .build();
            appt.getItems().add(ai);
        }

        return toResponse(appointmentRepository.save(appt));
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appt = getOrThrow(id);
        appt.setStatus(status);
        return toResponse(appointmentRepository.save(appt));
    }

    @Transactional
    public void cancel(Long id) {
        Appointment appt = getOrThrow(id);
        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Završeni termini se ne mogu otkazati");
        }
        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
    }

    private Appointment getOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Termin sa ID=" + id + " nije pronađen"));
    }

    private AppointmentResponse toResponse(Appointment a) {
        AppointmentResponse r = modelMapper.map(a, AppointmentResponse.class);
        r.setItems(a.getItems().stream()
                .map(i -> modelMapper.map(i, AppointmentItemResponse.class)).toList());
        return r;
    }
}
