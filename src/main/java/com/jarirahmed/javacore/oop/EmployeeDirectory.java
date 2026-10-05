package com.jarirahmed.javacore.oop;

import com.jarirahmed.javacore.exceptions.EmployeeNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A small in-memory repository used by the CLI and several lessons. */
public final class EmployeeDirectory {
    private final Map<Integer, Employee> employees = new HashMap<>();

    public void add(Employee employee) {
        if (employees.putIfAbsent(employee.getId(), employee) != null) {
            throw new IllegalArgumentException(
                    "An employee with id %d already exists.".formatted(employee.getId()));
        }
    }

    public Employee findById(int id) throws EmployeeNotFoundException {
        Employee employee = employees.get(id);
        if (employee == null) {
            throw new EmployeeNotFoundException(id);
        }
        return employee;
    }

    public List<Employee> activeEmployees() {
        return employees.values().stream()
                .filter(Employee::isActive)
                .sorted(Comparator.comparing(Employee::getName))
                .toList();
    }

    public List<Employee> allEmployeesSortedById() {
        List<Employee> result = new ArrayList<>(employees.values());
        result.sort(Comparator.comparingInt(Employee::getId));
        return result;
    }

    public int size() {
        return employees.size();
    }
}
