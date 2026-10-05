package com.jarirahmed.javacore.basics;

import com.jarirahmed.javacore.oop.EmploymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BasicsAndControlFlowTest {
    @Test
    void arithmetic_and_conditions_return_expected_values() {
        BasicsAndControlFlow.ArithmeticResult result =
                BasicsAndControlFlow.calculateArithmetic(10, 3);

        assertEquals(13, result.sum());
        assertEquals(7, result.difference());
        assertEquals(30, result.product());
        assertEquals(3, result.quotient());
        assertEquals(1, result.remainder());
        assertEquals(BasicsAndControlFlow.Grade.B, BasicsAndControlFlow.gradeFor(85));
        assertEquals("Grade: C (Passed)", BasicsAndControlFlow.describeResult(75));
    }

    @Test
    void loops_arrays_switch_and_input_normalization_are_deterministic() {
        assertEquals(15, BasicsAndControlFlow.sumUsingForLoop(1, 5));
        assertArrayEquals(new int[]{3, 2, 1}, BasicsAndControlFlow.countdown(3));
        assertArrayEquals(new int[]{20, 30, 40}, BasicsAndControlFlow.middleValues());
        assertEquals("Currently employed",
                BasicsAndControlFlow.describeEmploymentStatus(EmploymentStatus.ACTIVE));
        assertEquals("Jarir", BasicsAndControlFlow.formatInput("  Jarir "));
        assertEquals("Anonymous", BasicsAndControlFlow.formatInput(" "));
    }

    @Test
    void invalid_arithmetic_inputs_are_rejected() {
        assertThrows(IllegalArgumentException.class,
                () -> BasicsAndControlFlow.calculateArithmetic(1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> BasicsAndControlFlow.gradeFor(101));
    }
}
