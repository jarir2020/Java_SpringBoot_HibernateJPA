package com.jarirahmed.javacore.oop;

/** A simple concrete implementation used by the command-line lesson. */
public final class ConsoleNotificationSender implements NotificationSender {
    @Override
    public String send(String message) {
        return "Notification: " + message;
    }
}
