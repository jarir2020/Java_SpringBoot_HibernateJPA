package com.jarirahmed.javacore.collections;

import com.jarirahmed.javacore.oop.Employee;
import com.jarirahmed.javacore.oop.FullTimeEmployee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionExamplesTest {
    @Test
    void queues_and_deques_have_different_orders() {
        List<String> tasks = List.of("first", "second", "third");

        assertEquals(List.of("first", "second", "third"),
                CollectionExamples.processQueue(tasks));
        assertEquals(List.of("third", "second", "first"),
                CollectionExamples.unwindStack(tasks));
    }

    @Test
    void sets_maps_iterators_and_comparators_are_useful_for_domain_data() {
        Employee bob = new FullTimeEmployee(2, "Bob", new BigDecimal("3000"));
        Employee alice = new FullTimeEmployee(1, "Alice", new BigDecimal("4000"));

        assertEquals(List.of("Java", "Maven", "SQL"),
                List.copyOf(CollectionExamples.uniqueSortedSkills(
                        List.of("SQL", "Java", "Java", "Maven"))));
        assertEquals("Alice", CollectionExamples.namesById(List.of(bob, alice)).get(1));
        assertEquals(List.of(2, 4), CollectionExamples.doubleWithIterator(List.of(1, 2)));
        assertTrue(CollectionExamples.sortedByName(List.of(bob, alice)).getFirst().equals(alice));
    }
}
