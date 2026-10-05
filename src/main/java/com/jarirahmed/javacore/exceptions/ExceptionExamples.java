package com.jarirahmed.javacore.exceptions;

/** Checked, unchecked, and cleanup examples kept separate from domain classes. */
public final class ExceptionExamples {
    private ExceptionExamples() {
    }

    public static int parsePositiveNumber(String rawNumber) {
        int number = Integer.parseInt(rawNumber.trim());
        if (number <= 0) {
            throw new IllegalArgumentException("The number must be positive.");
        }
        return number;
    }

    public static int divideWithCleanup(int numerator, int denominator, Runnable cleanup) {
        try {
            return numerator / denominator;
        } catch (ArithmeticException exception) {
            // The caller receives a useful domain-neutral result after handling the failure.
            return 0;
        } finally {
            // finally runs whether division succeeds or throws.
            cleanup.run();
        }
    }

    public static String explainLookup(EmployeeNotFoundException exception) {
        return "Lookup failed: " + exception.getMessage();
    }
}
