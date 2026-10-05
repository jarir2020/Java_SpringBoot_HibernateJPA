package com.jarirahmed.javacore.advanced;

import com.jarirahmed.javacore.oop.FullTimeEmployee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrencyExamplesTest {
    @Test
    void synchronized_counter_protects_shared_updates() throws Exception {
        assertEquals(300, ConcurrencyExamples.runParallelCounter(3, 100));
    }

    @Test
    void completable_future_produces_a_later_result() {
        FullTimeEmployee employee = new FullTimeEmployee(1, "Alice", new BigDecimal("5000"));

        assertEquals("1:Alice",
                ConcurrencyExamples.loadEmployeeLabelAsync(employee).join());
    }
}
