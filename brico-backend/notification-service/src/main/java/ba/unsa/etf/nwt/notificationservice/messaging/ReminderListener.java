package ba.unsa.etf.nwt.notificationservice.messaging;

import ba.unsa.etf.nwt.notificationservice.client.UserClient;
import ba.unsa.etf.nwt.notificationservice.model.Notification;
import ba.unsa.etf.nwt.notificationservice.repository.NotificationRepository;
import ba.unsa.etf.nwt.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderListener {

    private final NotificationRepository notificationRepository;
    private final EmailService           emailService;
    private final UserClient             userClient;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy. 'u' HH:mm");

    @RabbitListener(queues = RabbitConfig.REMINDER_QUEUE)
    public void onReminder(AppointmentReminderEvent event) {
        log.info("Podsjetnik {} za appointmentId={}, clientId={}",
                event.getReminderType(), event.getAppointmentId(), event.getClientId());

        // Dohvati email klijenta ako nije proslijeđen
        String clientEmail = event.getClientEmail();
        if ((clientEmail == null || clientEmail.isBlank()) && event.getClientId() != null) {
            try {
                Map<String, Object> data = userClient.getUser(event.getClientId());
                clientEmail = (String) data.get("email");
            } catch (Exception e) {
                log.warn("Nije moguće dohvatiti email za podsjetnik, clientId={}", event.getClientId());
            }
        }

        String formattedTime = event.getStartTime() != null
                ? event.getStartTime().format(DISPLAY_FMT)
                : "";

        String notifMsg = "1h".equals(event.getReminderType())
                ? "Podsjetnik: imate termin za 1 sat u salonu " + nvl(event.getSalonName()) + " (" + formattedTime + ")."
                : "Podsjetnik: imate termin sutra u salonu "    + nvl(event.getSalonName()) + " (" + formattedTime + ").";

        // Spremi in-app notifikaciju
        notificationRepository.save(Notification.builder()
                .userId(event.getClientId())
                .type("APPOINTMENT_REMINDER")
                .title("1h".equals(event.getReminderType())
                        ? "Termin za 1 sat ⏰"
                        : "Termin sutra ⏰")
                .message(notifMsg)
                .referenceId(event.getAppointmentId())
                .isRead(false)
                .build());

        // Pošalji email podsjetnik
        Map<String, Object> vars = new HashMap<>();
        vars.put("clientName",      nvl(event.getClientName()));
        vars.put("hairdresserName", nvl(event.getHairdresserName()));
        vars.put("salonName",       nvl(event.getSalonName()));
        vars.put("salonAddress",    nvl(event.getSalonAddress()));
        vars.put("servicesSummary", nvl(event.getServicesSummary()));
        vars.put("startTime",       formattedTime);
        vars.put("totalPrice",      event.getTotalPrice() != null ? event.getTotalPrice() : "—");
        vars.put("reminderType",    event.getReminderType());

        emailService.sendAppointmentReminder(clientEmail, vars, event.getReminderType());
    }

    private String nvl(String s) { return s != null ? s : ""; }
}
