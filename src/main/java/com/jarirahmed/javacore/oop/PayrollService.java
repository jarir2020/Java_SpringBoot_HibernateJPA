package com.jarirahmed.javacore.oop;

import java.math.BigDecimal;

/**
 * Composition example: the service receives a notification capability instead
 * of constructing a concrete sender itself.
 */
public final class PayrollService {
    private final NotificationSender notificationSender;

    public PayrollService(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    public BigDecimal calculatePay(Employee employee) {
        return employee.monthlyPay();
    }

    public String notifyPayProcessed(Employee employee) {
        String message = "Pay processed for %s: %s".formatted(
                employee.getName(), employee.monthlyPay());
        return notificationSender.send(message);
    }
}
