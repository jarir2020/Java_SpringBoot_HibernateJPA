package com.jarirahmed.javacore.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreApiExamplesTest {
    @Test
    void core_apis_transform_and_validate_values() {
        assertEquals("Welcome, Jarir. Visit #2",
                CoreApiExamples.buildWelcomeMessage(" Jarir ", 2));
        assertEquals(42, CoreApiExamples.parseEmployeeId(" 42 "));
        assertEquals("2026-10-05", CoreApiExamples.formatDate(LocalDate.of(2026, 10, 5)));
        assertEquals(100, CoreApiExamples.clamp(120, 0, 100));
        assertTrue(CoreApiExamples.isValidUsername("jarir_2026"));
        assertFalse(CoreApiExamples.isValidUsername("not valid"));
    }

    @Test
    void invalid_clamp_bounds_are_rejected() {
        assertThrows(IllegalArgumentException.class,
                () -> CoreApiExamples.clamp(1, 10, 0));
    }
}
