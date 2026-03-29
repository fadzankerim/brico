package com.nwt.notificationservice.repository;

import com.nwt.notificationservice.model.Notification;
import com.nwt.notificationservice.model.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    List<Notification> findByStatusAndScheduledAtBetween(
            NotificationStatus status,
            LocalDateTime from,
            LocalDateTime to
    );
}
