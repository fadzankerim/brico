package ba.unsa.etf.nwt.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.from:noreply@brico.ba}")
    private String fromAddress;

    @Value("${mail.from-name:Brico.ba}")
    private String fromName;

    public void sendAppointmentConfirmation(String to, Map<String, Object> vars) {
        send(to, "✓ Rezervacija potvrđena — Brico.ba", "emails/confirmation-client", vars);
    }

    public void sendNewBookingToHairdresser(String to, Map<String, Object> vars) {
        send(to, "Nova rezervacija — Brico.ba", "emails/new-booking-hairdresser", vars);
    }

    public void sendNewBookingToOwner(String to, Map<String, Object> vars) {
        send(to, "Nova rezervacija u salonu — Brico.ba", "emails/new-booking-owner", vars);
    }

    public void sendAppointmentReminder(String to, Map<String, Object> vars, String reminderType) {
        String subject = "1h".equals(reminderType)
                ? "⏰ Podsjetnik: termin za 1 sat — Brico.ba"
                : "⏰ Podsjetnik: termin sutra — Brico.ba";
        send(to, subject, "emails/reminder-client", vars);
    }

    public void sendCancellation(String to, Map<String, Object> vars) {
        send(to, "Termin otkazan — Brico.ba", "emails/cancellation-client", vars);
    }

    private void send(String to, String subject, String template, Map<String, Object> vars) {
        if (to == null || to.isBlank()) {
            log.warn("Email nije poslan — adresa primatelja je null ili prazna (template={})", template);
            return;
        }
        try {
            Context ctx = new Context();
            if (vars != null) vars.forEach(ctx::setVariable);

            String html = templateEngine.process(template, ctx);

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(msg);
            log.info("Email poslan na {} (template={})", to, template);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Greška pri slanju emaila na {} (template={}): {}", to, template, e.getMessage());
        } catch (Exception e) {
            log.error("Neočekivana greška pri slanju emaila na {}: {}", to, e.getMessage());
        }
    }
}
