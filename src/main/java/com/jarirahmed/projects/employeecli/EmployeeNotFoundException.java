package com.jarirahmed.projects.employeecli;

/** A named domain failure is easier for the CLI boundary to explain. */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(int employeeId) {
        super("No employee exists with id " + employeeId + ".");
    }
}
