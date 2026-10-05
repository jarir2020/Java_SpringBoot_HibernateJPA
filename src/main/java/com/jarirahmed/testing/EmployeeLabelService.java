package com.jarirahmed.testing;

import java.util.Objects;

/**
 * A deliberately small class for teaching unit testing.
 *
 * The service owns the label formatting rule, while the port owns data access.
 * A unit test can therefore mock the port and test this rule without starting
 * Spring, opening a database, or making an HTTP request.
 */
public final class EmployeeLabelService {
    private final EmployeeDirectoryPort directory;

    public EmployeeLabelService(EmployeeDirectoryPort directory) {
        this.directory = Objects.requireNonNull(directory, "directory");
    }

    public String labelFor(long employeeId) {
        EmployeeSummary employee = directory.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee " + employeeId + " was not found."));
        return employee.fullName() + " [" + employee.department() + "]";
    }
}
