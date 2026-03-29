package com.nwt.notificationservice.scheduler;

import com.nwt.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;

    /**
     * Runs every 60 seconds.
     *
     * In a real system this would call appointment-service via Feign to discover appointments
     * starting in ~24h or ~1h and create reminder notifications.
     *
     * For now it queries the local notification DB for PENDING notifications whose
     * scheduledAt falls within the next 2 hours, logs the intended action, and marks them SENT.
     */
    @Scheduled(fixedRate = 60_000)
    public void checkUpcomingReminders() {
        log.info("Scheduler: checking for upcoming appointment reminders...");
        try {
            notificationService.processUpcomingReminders();
        } catch (Exception ex) {
            log.error("Scheduler: error while processing upcoming reminders", ex);
        }
    }
}
