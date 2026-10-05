package com.jarirahmed.projects.employeecli;

/** Raised when an add operation would reuse an existing employee id. */
public class DuplicateEmployeeException extends RuntimeException {
    public DuplicateEmployeeException(int employeeId) {
        super("An employee with id " + employeeId + " already exists.");
    }
}
