package com.jarirahmed.javacore.functional;

import com.jarirahmed.javacore.oop.Employee;
import com.jarirahmed.javacore.oop.FullTimeEmployee;
import com.jarirahmed.javacore.oop.PartTimeEmployee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionalExamplesTest {
    @Test
    void streams_filter_map_sort_and_reduce_domain_values() {
        Employee alice = new FullTimeEmployee(1, "Alice", new BigDecimal("5000"));
        Employee bob = new PartTimeEmployee(2, "Bob", new BigDecimal("25"), 80);
        bob.deactivate();

        assertEquals(List.of("Alice"), FunctionalExamples.activeEmployeeNames(List.of(bob, alice)));
        assertEquals(new BigDecimal("7000"),
                FunctionalExamples.totalMonthlyPay(List.of(alice, bob)));
        assertTrue(FunctionalExamples.findByName(List.of(alice, bob), "ALICE").isPresent());
        assertEquals("1:Alice", FunctionalExamples.formatEmployee(
                alice, employee -> employee.getId() + ":" + employee.getName()));
    }
}
