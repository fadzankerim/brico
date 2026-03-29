package com.nwt.notificationservice.service;

import com.nwt.notificationservice.dto.request.CreateNotificationRequest;
import com.nwt.notificationservice.dto.request.UpdateNotificationStatusRequest;
import com.nwt.notificationservice.dto.response.NotificationResponse;
import com.nwt.notificationservice.messaging.events.AppointmentBookedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse createNotification(CreateNotificationRequest request);

    NotificationResponse getNotificationById(Long id);

    Page<NotificationResponse> getNotificationsByUserId(Long userId, Pageable pageable);

    NotificationResponse updateNotificationStatus(Long id, UpdateNotificationStatusRequest request);

    /**
     * Saga step: create a booking notification from an AppointmentBookedEvent.
     * Publishes success or failure saga events internally.
     */
    void createBookingNotification(AppointmentBookedEvent event);

    /**
     * Called by the scheduler: process PENDING notifications due within the next two hours.
     */
    void processUpcomingReminders();
}
