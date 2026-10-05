package com.jarirahmed.javacore.functional;

import com.jarirahmed.javacore.oop.Employee;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/** Lambdas, method references, streams, and Optional in small examples. */
public final class FunctionalExamples {
    private FunctionalExamples() {
    }

    public static List<String> activeEmployeeNames(Collection<Employee> employees) {
        Predicate<Employee> active = Employee::isActive;
        return employees.stream()
                .filter(active)
                .map(Employee::getName)
                .sorted()
                .toList();
    }

    public static BigDecimal totalMonthlyPay(Collection<Employee> employees) {
        return employees.stream()
                .map(Employee::monthlyPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Optional<Employee> findByName(Collection<Employee> employees, String name) {
        return employees.stream()
                .filter(employee -> employee.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public static String formatEmployee(Employee employee, Function<Employee, String> formatter) {
        return formatter.apply(employee);
    }
}
