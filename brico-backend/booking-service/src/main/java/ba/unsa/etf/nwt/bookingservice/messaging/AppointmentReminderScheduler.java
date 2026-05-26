package ba.unsa.etf.nwt.bookingservice.messaging;

import ba.unsa.etf.nwt.bookingservice.model.Appointment;
import ba.unsa.etf.nwt.bookingservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderScheduler {

    private final AppointmentRepository    appointmentRepository;
    private final AppointmentEventPublisher eventPublisher;

    // Pokretanje svake minute
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        send24hReminders(now);
        send1hReminders(now);
    }

    private void send24hReminders(LocalDateTime now) {
        // Prozor: [now+23h55m, now+24h5m] — tolerance ±5min da se pokrije svaki minute-tick
        LocalDateTime from = now.plusHours(23).plusMinutes(55);
        LocalDateTime to   = now.plusHours(24).plusMinutes(5);

        List<Appointment> list = appointmentRepository.findForReminder24h(from, to);
        for (Appointment appt : list) {
            try {
                eventPublisher.publishReminder(buildEvent(appt, "24h"));
                appt.setReminder24hSent(true);
                appointmentRepository.save(appt);
                log.info("24h podsjetnik poslan za appointmentId={}", appt.getId());
            } catch (Exception e) {
                log.error("Greška pri slanju 24h podsjetnika za appointmentId={}: {}", appt.getId(), e.getMessage());
            }
        }
    }

    private void send1hReminders(LocalDateTime now) {
        // Prozor: [now+55m, now+65m]
        LocalDateTime from = now.plusMinutes(55);
        LocalDateTime to   = now.plusMinutes(65);

        List<Appointment> list = appointmentRepository.findForReminder1h(from, to);
        for (Appointment appt : list) {
            try {
                eventPublisher.publishReminder(buildEvent(appt, "1h"));
                appt.setReminder1hSent(true);
                appointmentRepository.save(appt);
                log.info("1h podsjetnik poslan za appointmentId={}", appt.getId());
            } catch (Exception e) {
                log.error("Greška pri slanju 1h podsjetnika za appointmentId={}: {}", appt.getId(), e.getMessage());
            }
        }
    }

    private AppointmentReminderEvent buildEvent(Appointment appt, String type) {
        String servicesSummary = appt.getItems().stream()
                .map(i -> i.getServiceName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return AppointmentReminderEvent.builder()
                .appointmentId(appt.getId())
                .clientId(appt.getClientId())
                .clientEmail(null) // notification-service će dohvatiti via UserClient
                .clientName(appt.getClientName())
                .hairdresserName(appt.getHairdresserName())
                .salonName(appt.getSalonName())
                .salonAddress(appt.getSalonAddress())
                .startTime(appt.getStartTime())
                .totalPrice(appt.getTotalPrice())
                .servicesSummary(servicesSummary)
                .reminderType(type)
                .build();
    }
}
