package com.jarirahmed.javacore.generics;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenericExamplesTest {
    @Test
    void generic_box_and_methods_preserve_type_information() {
        GenericExamples.Box<String> box = new GenericExamples.Box<>("Java");

        assertEquals("Java", box.value());
        assertEquals("z", GenericExamples.greatest("a", "z"));
        assertEquals(6.0, GenericExamples.sumNumbers(List.of(1, 2.0, 3L)));
    }

    @Test
    void wildcard_bounds_support_reading_and_writing_safely() {
        List<Integer> ids = new ArrayList<>();
        GenericExamples.addDefaultIds(ids, 3);

        assertEquals(List.of(1, 2, 3), ids);
        assertEquals(1, GenericExamples.firstItem(ids));
        assertThrows(NoSuchElementException.class,
                () -> GenericExamples.firstItem(List.of()));
    }
}
