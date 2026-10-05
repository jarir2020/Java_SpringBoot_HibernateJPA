package com.jarirahmed.projects.employeecli;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * The Project 1 domain object. State is private and can change only through
 * methods that preserve the record's validation rules.
 */
public final class EmployeeRecord {
    private final int id;
    private String name;
    private String department;
    private BigDecimal monthlySalary;
    private EmployeeStatus status;

    public EmployeeRecord(
            int id,
            String name,
            String department,
            BigDecimal monthlySalary) {
        this(id, name, department, monthlySalary, EmployeeStatus.ACTIVE);
    }

    private EmployeeRecord(
            int id,
            String name,
            String department,
            BigDecimal monthlySalary,
            EmployeeStatus status) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee id must be positive.");
        }
        this.id = id;
        this.name = validateText(name, "Employee name");
        this.department = validateText(department, "Department");
        if (monthlySalary == null || monthlySalary.signum() < 0) {
            throw new IllegalArgumentException("Monthly salary cannot be negative.");
        }
        this.monthlySalary = monthlySalary;
        this.status = Objects.requireNonNull(status, "status");
    }

    public static EmployeeRecord restored(
            int id,
            String name,
            String department,
            BigDecimal monthlySalary,
            EmployeeStatus status) {
        return new EmployeeRecord(id, name, department, monthlySalary, status);
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String department() {
        return department;
    }

    public BigDecimal monthlySalary() {
        return monthlySalary;
    }

    public EmployeeStatus status() {
        return status;
    }

    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE;
    }

    public void rename(String newName) {
        name = validateText(newName, "Employee name");
    }

    public void changeDepartment(String newDepartment) {
        department = validateText(newDepartment, "Department");
    }

    public void changeSalary(BigDecimal newSalary) {
        if (newSalary == null || newSalary.signum() < 0) {
            throw new IllegalArgumentException("Monthly salary cannot be negative.");
        }
        monthlySalary = newSalary;
    }

    public void deactivate() {
        status = EmployeeStatus.INACTIVE;
    }

    public void activate() {
        status = EmployeeStatus.ACTIVE;
    }

    public String summary() {
        return "%d | %s | %s | %s | %s".formatted(
                id, name, department, monthlySalary, status);
    }

    /** The simple file format is intentionally visible for this beginner project. */
    String toFileLine() {
        return "%d|%s|%s|%s|%s".formatted(
                id, name, department, monthlySalary, status);
    }

    static EmployeeRecord fromFileLine(String line, int lineNumber) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 5) {
            throw malformed(lineNumber, "expected five pipe-separated fields");
        }

        try {
            return restored(
                    Integer.parseInt(fields[0].trim()),
                    fields[1],
                    fields[2],
                    new BigDecimal(fields[3].trim()),
                    EmployeeStatus.valueOf(fields[4].trim()));
        } catch (RuntimeException exception) {
            throw malformed(lineNumber, "contains an invalid employee value", exception);
        }
    }

    private static String validateText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank.");
        }
        String trimmed = value.trim();
        if (trimmed.contains("|") || trimmed.contains("\n") || trimmed.contains("\r")) {
            throw new IllegalArgumentException(field + " cannot contain '|', newline, or carriage return.");
        }
        return trimmed;
    }

    private static EmployeeStorageException malformed(int lineNumber, String reason) {
        return new EmployeeStorageException("Invalid employee file at line "
                + lineNumber + ": " + reason + ".");
    }

    private static EmployeeStorageException malformed(
            int lineNumber,
            String reason,
            Throwable cause) {
        return new EmployeeStorageException("Invalid employee file at line "
                + lineNumber + ": " + reason + ".", cause);
    }
}
