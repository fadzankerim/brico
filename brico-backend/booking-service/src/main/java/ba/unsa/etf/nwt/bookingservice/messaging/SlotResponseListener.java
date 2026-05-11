package ba.unsa.etf.nwt.bookingservice.messaging;

import ba.unsa.etf.nwt.bookingservice.model.Appointment;
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

    private final AppointmentRepository appointmentRepository;
    private final AppointmentEventPublisher eventPublisher;

    // Salon je rezervisao slot — potvrđujemo termin i šaljemo notifikaciju klijentu
    @RabbitListener(queues = RabbitConfig.SLOT_RESERVED_QUEUE)
    public void onSlotReserved(SlotResponseEvent event) {
        log.info("Slot rezervisan za appointmentId={} — potvrđujem termin", event.getAppointmentId());
        appointmentRepository.findById(event.getAppointmentId()).ifPresent(appointment -> {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(appointment);
            log.info("Termin {} potvrđen (CONFIRMED)", event.getAppointmentId());

            String timeStr = appointment.getStartTime()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            eventPublisher.publishNotification(
                    appointment.getClientId(),
                    "APPOINTMENT_CONFIRMED",
                    "Termin potvrđen ✓",
                    "Vaš termin u salonu " + appointment.getSalonName() + " (" + timeStr + ") je potvrđen.",
                    appointment.getId());
        });
    }

    // Salon je odbio slot — kompenzacijska akcija: otkazujemo termin i obavještavamo klijenta
    @RabbitListener(queues = RabbitConfig.SLOT_REJECTED_QUEUE)
    public void onSlotRejected(SlotResponseEvent event) {
        log.warn("Slot odbijen za appointmentId={}: {} — otkazujem termin",
                event.getAppointmentId(), event.getReason());
        appointmentRepository.findById(event.getAppointmentId()).ifPresent(appointment -> {
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            log.warn("Termin {} otkazan (CANCELLED) — kompenzacijska akcija", event.getAppointmentId());

            eventPublisher.publishNotification(
                    appointment.getClientId(),
                    "APPOINTMENT_CANCELLED",
                    "Termin nije moguć ✗",
                    "Nažalost, frizer je zauzet u traženom terminu u salonu " + appointment.getSalonName() +
                    ". Molimo odaberite drugi termin.",
                    appointment.getId());
        });
    }
}
