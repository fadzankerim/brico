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
public class NotificationListener {

    private final NotificationRepository notificationRepository;
    private final EmailService           emailService;
    private final UserClient             userClient;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy. 'u' HH:mm");

    // ── Termin potvrđen ───────────────────────────────────────────────
    @RabbitListener(queues = RabbitConfig.NOTIF_CONFIRMED_QUEUE)
    public void onAppointmentConfirmed(SlotResponseEvent event) {
        if (event.getClientId() == null) return;
        log.info("Notifikacija: termin {} potvrđen za klijenta {}", event.getAppointmentId(), event.getClientId());

        String formattedTime = formatTime(event.getStartTime());

        notificationRepository.save(Notification.builder()
                .userId(event.getClientId())
                .type("APPOINTMENT_CONFIRMED")
                .title("Termin potvrđen ✓")
                .message("Vaš termin u salonu " + nvl(event.getSalonName()) +
                         (event.getStartTime() != null ? " (" + formattedTime + ")" : "") +
                         " je potvrđen.")
                .referenceId(event.getAppointmentId())
                .isRead(false)
                .build());

        // Email klijentu — potvrda rezervacije
        Map<String, Object> vars = buildConfirmationVars(event, formattedTime);
        emailService.sendAppointmentConfirmation(event.getClientEmail(), vars);

        // Email frizeru — nova rezervacija
        if (event.getHairdresserUserId() != null) {
            String hairdresserEmail = fetchEmail(event.getHairdresserUserId());
            Map<String, Object> hdVars = new HashMap<>(vars);
            hdVars.put("hairdresserName", nvl(event.getHairdresserName()));
            emailService.sendNewBookingToHairdresser(hairdresserEmail, hdVars);
        }

        // Email vlasniku salona — nova rezervacija
        if (event.getSalonOwnerId() != null) {
            String ownerEmail = fetchEmail(event.getSalonOwnerId());
            emailService.sendNewBookingToOwner(ownerEmail, vars);
        }
    }

    // ── Termin odbijen ────────────────────────────────────────────────
    @RabbitListener(queues = RabbitConfig.NOTIF_CANCELLED_QUEUE)
    public void onAppointmentCancelled(SlotResponseEvent event) {
        if (event.getClientId() == null) return;
        log.info("Notifikacija: termin {} odbijen za klijenta {}", event.getAppointmentId(), event.getClientId());

        notificationRepository.save(Notification.builder()
                .userId(event.getClientId())
                .type("APPOINTMENT_CANCELLED")
                .title("Termin nije moguć ✗")
                .message("Nažalost, frizer je zauzet u traženom terminu" +
                         (event.getSalonName() != null ? " (" + event.getSalonName() + ")" : "") +
                         ". Molimo odaberite drugi termin.")
                .referenceId(event.getAppointmentId())
                .isRead(false)
                .build());

        Map<String, Object> vars = new HashMap<>();
        vars.put("clientName",      nvl(event.getClientName()));
        vars.put("salonName",       nvl(event.getSalonName()));
        vars.put("hairdresserName", nvl(event.getHairdresserName()));
        vars.put("startTime",       formatTime(event.getStartTime()));
        vars.put("cancelReason",    event.getReason());
        emailService.sendCancellation(event.getClientEmail(), vars);
    }

    // ── Generička notifikacija (otkazivanje od strane salona i sl.) ───
    @RabbitListener(queues = RabbitConfig.NOTIF_PUSH_QUEUE)
    public void onPushNotification(NotificationEvent event) {
        log.info("Push notifikacija za userId={}: {}", event.getUserId(), event.getTitle());

        notificationRepository.save(Notification.builder()
                .userId(event.getUserId())
                .type(event.getType())
                .title(event.getTitle())
                .message(event.getMessage())
                .referenceId(event.getReferenceId())
                .isRead(false)
                .build());

        if ("APPOINTMENT_CANCELLED".equals(event.getType()) && event.getRecipientEmail() != null) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("clientName",   resolveClientName(event.getUserId()));
            vars.put("salonName",    "");
            vars.put("hairdresserName", "");
            vars.put("startTime",    "");
            vars.put("cancelReason", event.getMessage());
            emailService.sendCancellation(event.getRecipientEmail(), vars);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private Map<String, Object> buildConfirmationVars(SlotResponseEvent event, String formattedTime) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("clientName",      nvl(event.getClientName()));
        vars.put("hairdresserName", nvl(event.getHairdresserName()));
        vars.put("salonName",       nvl(event.getSalonName()));
        vars.put("salonAddress",    nvl(event.getSalonAddress()));
        vars.put("servicesSummary", nvl(event.getServicesSummary()));
        vars.put("startTime",       formattedTime);
        vars.put("totalPrice",      event.getTotalPrice() != null ? event.getTotalPrice() : "—");
        return vars;
    }

    private String fetchEmail(Long userId) {
        try {
            Map<String, Object> data = userClient.getUser(userId);
            return (String) data.get("email");
        } catch (Exception e) {
            log.warn("Nije moguće dohvatiti email za userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    private String resolveClientName(Long userId) {
        try {
            Map<String, Object> data = userClient.getUser(userId);
            return data.getOrDefault("fullName", "Klijent").toString();
        } catch (Exception e) {
            return "Klijent";
        }
    }

    private String nvl(String s) { return s != null ? s : ""; }

    private String formatTime(String isoDateTime) {
        if (isoDateTime == null) return "";
        try {
            return isoDateTime.substring(0, 16).replace("T", " u ");
        } catch (Exception e) {
            return isoDateTime;
        }
    }
}
