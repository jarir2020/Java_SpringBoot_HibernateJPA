package com.jarirahmed.javacore.generics;

import java.util.List;
import java.util.NoSuchElementException;

/** Generic methods and wildcard bounds used by later repository code. */
public final class GenericExamples {
    private GenericExamples() {
    }

    public static final class Box<T> {
        private final T value;

        public Box(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }
    }

    public static <T> T firstItem(List<T> items) {
        if (items.isEmpty()) {
            throw new NoSuchElementException("The list has no first item.");
        }
        return items.getFirst();
    }

    public static <T extends Comparable<? super T>> T greatest(T first, T second) {
        return first.compareTo(second) >= 0 ? first : second;
    }

    /** A producer can be read as Number regardless of its concrete numeric type. */
    public static double sumNumbers(List<? extends Number> numbers) {
        double total = 0;
        for (Number number : numbers) {
            total += number.doubleValue();
        }
        return total;
    }

    /** A consumer of integers can accept List<Integer> or List<Number>. */
    public static void addDefaultIds(List<? super Integer> target, int count) {
        for (int id = 1; id <= count; id++) {
            target.add(id);
        }
    }
}
