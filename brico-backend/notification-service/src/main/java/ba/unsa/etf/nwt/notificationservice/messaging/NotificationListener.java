package ba.unsa.etf.nwt.notificationservice.messaging;

import ba.unsa.etf.nwt.notificationservice.model.Notification;
import ba.unsa.etf.nwt.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationRepository notificationRepository;

    // Termin potvrđen — obavijesti klijenta
    @RabbitListener(queues = RabbitConfig.NOTIF_CONFIRMED_QUEUE)
    public void onAppointmentConfirmed(SlotResponseEvent event) {
        if (event.getClientId() == null) return;
        log.info("Notifikacija: termin {} potvrđen za klijenta {}", event.getAppointmentId(), event.getClientId());

        notificationRepository.save(Notification.builder()
                .userId(event.getClientId())
                .type("APPOINTMENT_CONFIRMED")
                .title("Termin potvrđen ✓")
                .message("Vaš termin u salonu " + nvl(event.getSalonName()) +
                         (event.getStartTime() != null ? " (" + formatTime(event.getStartTime()) + ")" : "") +
                         " je potvrđen.")
                .referenceId(event.getAppointmentId())
                .isRead(false)
                .build());
    }

    // Termin odbijen — obavijesti klijenta
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
    }

    // Generička notifikacija (za vlasnika salona i sl.)
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
    }

    private String nvl(String s) { return s != null ? s : ""; }

    private String formatTime(String isoDateTime) {
        try {
            return isoDateTime.substring(0, 16).replace("T", " u ");
        } catch (Exception e) {
            return isoDateTime;
        }
    }
}
