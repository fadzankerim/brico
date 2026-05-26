package ba.unsa.etf.nwt.bookingservice.messaging;

import ba.unsa.etf.nwt.bookingservice.model.AppointmentStatus;
import ba.unsa.etf.nwt.bookingservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlotResponseListener {

    private final AppointmentRepository  appointmentRepository;

    // Salon je rezervisao slot — potvrđujemo status termina
    // Notifikacije klijentu/frizeru/vlasniku šalje notification-service direktno
    @RabbitListener(queues = RabbitConfig.SLOT_RESERVED_QUEUE)
    public void onSlotReserved(SlotResponseEvent event) {
        log.info("Slot rezervisan za appointmentId={} — potvrđujem termin", event.getAppointmentId());
        appointmentRepository.findById(event.getAppointmentId()).ifPresent(appointment -> {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(appointment);
            log.info("Termin {} potvrđen (CONFIRMED)", event.getAppointmentId());
        });
    }

    // Salon je odbio slot — kompenzacijska akcija: otkazujemo termin
    // Notifikacija klijentu šalje notification-service direktno
    @RabbitListener(queues = RabbitConfig.SLOT_REJECTED_QUEUE)
    public void onSlotRejected(SlotResponseEvent event) {
        log.warn("Slot odbijen za appointmentId={}: {} — otkazujem termin",
                event.getAppointmentId(), event.getReason());
        appointmentRepository.findById(event.getAppointmentId()).ifPresent(appointment -> {
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            log.warn("Termin {} otkazan (CANCELLED) — kompenzacijska akcija", event.getAppointmentId());
        });
    }
}
