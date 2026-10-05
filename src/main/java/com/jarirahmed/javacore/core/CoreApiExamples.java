package com.jarirahmed.javacore.core;

import com.jarirahmed.javacore.oop.EmploymentStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/** Examples from Java's standard library that appear in backend code often. */
public final class CoreApiExamples {
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("[A-Za-z][A-Za-z0-9_]{2,19}");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private CoreApiExamples() {
    }

    public static String buildWelcomeMessage(String name, int visitCount) {
        StringBuilder message = new StringBuilder("Welcome, ");
        message.append(name.trim()).append(". Visit #").append(visitCount);
        return message.toString();
    }

    public static int parseEmployeeId(String rawId) {
        // Integer is a wrapper type; parseInt converts text into a primitive int.
        return Integer.parseInt(rawId.trim());
    }

    public static int clamp(int value, int minimum, int maximum) {
        if (minimum > maximum) {
            throw new IllegalArgumentException("Minimum cannot exceed maximum.");
        }
        return Math.max(minimum, Math.min(value, maximum));
    }

    public static String formatDate(LocalDate date) {
        return DATE_FORMATTER.format(date);
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static String describeStatus(EmploymentStatus status) {
        return switch (status) {
            case ACTIVE -> "active employee";
            case ON_LEAVE -> "employee on leave";
            case TERMINATED -> "former employee";
        };
    }
}
