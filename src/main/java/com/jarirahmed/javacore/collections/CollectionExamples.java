package com.jarirahmed.javacore.collections;

import com.jarirahmed.javacore.oop.Employee;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Examples of the collection types used throughout backend applications. */
public final class CollectionExamples {
    private CollectionExamples() {
    }

    public static List<String> processQueue(List<String> tasks) {
        Queue<String> queue = new LinkedList<>(tasks);
        List<String> processed = new ArrayList<>();
        while (!queue.isEmpty()) {
            processed.add(queue.remove());
        }
        return processed;
    }

    public static List<String> unwindStack(List<String> tasks) {
        Deque<String> stack = new ArrayDeque<>(tasks);
        List<String> unwound = new ArrayList<>();
        while (!stack.isEmpty()) {
            unwound.add(stack.removeLast());
        }
        return unwound;
    }

    public static Set<String> uniqueSortedSkills(Collection<String> skills) {
        Set<String> uniqueSkills = new HashSet<>(skills);
        return new TreeSet<>(uniqueSkills);
    }

    public static Map<Integer, String> namesById(Collection<Employee> employees) {
        Map<Integer, String> names = new TreeMap<>();
        for (Employee employee : employees) {
            names.put(employee.getId(), employee.getName());
        }
        return names;
    }

    public static List<String> namesSortedByLength(Collection<Employee> employees) {
        List<String> names = new ArrayList<>();
        for (Employee employee : employees) {
            names.add(employee.getName());
        }
        names.sort(Comparator.comparingInt(String::length).thenComparing(String::compareTo));
        return names;
    }

    public static List<Integer> doubleWithIterator(List<Integer> numbers) {
        List<Integer> doubled = new ArrayList<>();
        var iterator = numbers.iterator();
        while (iterator.hasNext()) {
            doubled.add(iterator.next() * 2);
        }
        return doubled;
    }

    /** A natural ordering can be supplied with a comparator at the call site. */
    public static List<Employee> sortedByName(Collection<Employee> employees) {
        return employees.stream()
                .sorted(Comparator.comparing(Employee::getName))
                .toList();
    }

    /** A HashMap is useful for direct lookup; this method makes that operation visible. */
    public static boolean containsId(Map<Integer, Employee> employees, int id) {
        return employees.containsKey(id);
    }
}
