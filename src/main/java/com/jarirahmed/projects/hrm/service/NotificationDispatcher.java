package com.jarirahmed.projects.hrm.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Demonstrates the hand-off from a committed database row to background work.
 * The local implementation marks a notification sent instead of calling email.
 */
@Service
public class NotificationDispatcher {
    private final AsyncNotificationWorker asyncNotificationWorker;

    public NotificationDispatcher(AsyncNotificationWorker asyncNotificationWorker) {
        this.asyncNotificationWorker = asyncNotificationWorker;
    }

    public void queueAfterCommit(Long notificationId) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    asyncNotificationWorker.dispatchAsync(notificationId);
                }
            });
        } else {
            asyncNotificationWorker.dispatchAsync(notificationId);
        }
    }
}
