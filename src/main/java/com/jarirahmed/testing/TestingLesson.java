package com.jarirahmed.testing;

import java.util.Optional;

/** Prints the testing pyramid before the executable examples run in tests. */
public final class TestingLesson {
    private TestingLesson() {
    }

    public static void run() {
        System.out.println("\n=== PHASE 11: TESTING ===");
        System.out.println("Unit: one class + mocked boundary, fast and isolated");
        System.out.println("Integration: Spring context + MockMvc + disposable H2 database");
        System.out.println("API: exercise the HTTP contract with a REST client or MockMvc");

        EmployeeDirectoryPort sampleDirectory = id -> id == 1
                ? Optional.of(new EmployeeSummary(1, "Alice Rahman", "Engineering"))
                : Optional.empty();
        System.out.println("Unit target example: "
                + new EmployeeLabelService(sampleDirectory).labelFor(1));
        System.out.println("Testcontainers template: disabled until Docker is intentionally enabled");
        System.out.println("PHASE 11 COMPLETE");
    }
}
