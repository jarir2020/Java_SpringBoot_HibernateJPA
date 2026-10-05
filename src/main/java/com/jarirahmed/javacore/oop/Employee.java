package com.jarirahmed.javacore.oop;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Abstract base class shared by the employee types.
 *
 * <p>The private fields protect the object's state. Subclasses provide the
 * part of the behavior that differs: how monthly pay is calculated.</p>
 */
public abstract class Employee {
    private final int id;
    private String name;
    private EmploymentStatus status;

    protected Employee(int id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee id must be positive.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be blank.");
        }
        this.id = id;
        this.name = name.trim();
        this.status = EmploymentStatus.ACTIVE;
    }

    public final int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final EmploymentStatus getStatus() {
        return status;
    }

    public final boolean isActive() {
        return status == EmploymentStatus.ACTIVE;
    }

    public final void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be blank.");
        }
        name = newName.trim();
    }

    public final void placeOnLeave() {
        status = EmploymentStatus.ON_LEAVE;
    }

    public final void deactivate() {
        status = EmploymentStatus.TERMINATED;
    }

    public abstract BigDecimal monthlyPay();

    public String summary() {
        return "%d - %s (%s)".formatted(id, name, status);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Employee employee)) {
            return false;
        }
        return id == employee.id;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}
