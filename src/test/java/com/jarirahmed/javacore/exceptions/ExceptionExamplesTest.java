package com.jarirahmed.javacore.exceptions;

import com.jarirahmed.javacore.oop.EmployeeDirectory;
import com.jarirahmed.javacore.oop.FullTimeEmployee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionExamplesTest {
    @Test
    void checked_lookup_failure_has_a_domain_specific_type() {
        EmployeeDirectory directory = new EmployeeDirectory();

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> directory.findById(99));

        assertEquals("Lookup failed: No employee exists with id 99.",
                ExceptionExamples.explainLookup(exception));
    }

    @Test
    void unchecked_input_failure_and_finally_cleanup_are_visible() {
        assertEquals(42, ExceptionExamples.parsePositiveNumber("42"));
        assertThrows(IllegalArgumentException.class,
                () -> ExceptionExamples.parsePositiveNumber("-1"));

        int[] cleanupCalls = {0};
        assertEquals(0, ExceptionExamples.divideWithCleanup(10, 0, () -> cleanupCalls[0]++));
        assertEquals(1, cleanupCalls[0]);
    }

    @Test
    void directory_can_store_a_valid_employee() throws Exception {
        EmployeeDirectory directory = new EmployeeDirectory();
        directory.add(new FullTimeEmployee(1, "Alice", new BigDecimal("5000")));
        assertEquals(1, directory.size());
    }
}
