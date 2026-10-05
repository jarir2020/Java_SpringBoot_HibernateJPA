package com.jarirahmed.projects.hrm.service;

import com.jarirahmed.projects.hrm.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Separate bean preserves the Spring proxy boundary required by @Async. */
@Service
public class AsyncNotificationWorker {
    private final NotificationRepository notificationRepository;

    public AsyncNotificationWorker(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Async("project5TaskExecutor")
    @Transactional
    public void dispatchAsync(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.markSent();
            notificationRepository.save(notification);
        });
    }
}
