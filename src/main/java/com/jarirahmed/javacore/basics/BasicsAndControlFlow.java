package com.jarirahmed.javacore.basics;

import com.jarirahmed.javacore.oop.EmploymentStatus;

import java.util.Arrays;

/**
 * Small, framework-free examples for the first Java concepts.
 *
 * <p>Methods return values instead of printing so the lesson can be reused by
 * both the command-line application and automated tests.</p>
 */
public final class BasicsAndControlFlow {
    private BasicsAndControlFlow() {
        // Utility classes are not instantiated.
    }

    public enum Grade {
        A, B, C, F
    }

    public record ArithmeticResult(int sum, int difference, int product,
                                   int quotient, int remainder) {
    }

    public static ArithmeticResult calculateArithmetic(int left, int right) {
        if (right == 0) {
            throw new IllegalArgumentException("The divisor cannot be zero.");
        }

        return new ArithmeticResult(
                left + right,
                left - right,
                left * right,
                left / right,
                left % right);
    }

    public static String describeCommonTypes() {
        int employeeId = 42;
        double weeklyHours = 40.0;
        boolean active = true;
        char grade = 'A';
        String name = "Jarir";

        return "%d:%.1f:%b:%c:%s".formatted(
                employeeId, weeklyHours, active, grade, name);
    }

    public static Grade gradeFor(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("A score must be between 0 and 100.");
        }

        if (score >= 90) {
            return Grade.A;
        }
        if (score >= 80) {
            return Grade.B;
        }
        if (score >= 70) {
            return Grade.C;
        }
        return Grade.F;
    }

    public static String describeResult(int score) {
        Grade grade = gradeFor(score);
        String status = score >= 70 ? "Passed" : "Failed";
        return "Grade: %s (%s)".formatted(grade, status);
    }

    public static String describeEmploymentStatus(EmploymentStatus status) {
        return switch (status) {
            case ACTIVE -> "Currently employed";
            case ON_LEAVE -> "Temporarily away";
            case TERMINATED -> "No longer employed";
        };
    }

    public static int sumUsingForLoop(int start, int endInclusive) {
        int total = 0;
        for (int number = start; number <= endInclusive; number++) {
            total += number;
        }
        return total;
    }

    public static int[] countdown(int startingNumber) {
        if (startingNumber < 0) {
            throw new IllegalArgumentException("The starting number cannot be negative.");
        }

        int[] result = new int[startingNumber];
        int index = 0;
        int current = startingNumber;
        while (current > 0) {
            result[index++] = current--;
        }
        return result;
    }

    public static int[] middleValues() {
        int[] numbers = {10, 20, 30, 40, 50};
        return Arrays.copyOfRange(numbers, 1, 4);
    }

    /**
     * Input/output belongs at the edge of an application. This method shows
     * the testable part: normalize a value before the CLI prints it.
     */
    public static String formatInput(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return "Anonymous";
        }
        return rawName.trim();
    }
}
