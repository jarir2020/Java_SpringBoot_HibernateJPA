package com.jarirahmed.javacore.oop;

/** A capability used to demonstrate interfaces and dependency composition. */
@FunctionalInterface
public interface NotificationSender {
    String send(String message);
}
