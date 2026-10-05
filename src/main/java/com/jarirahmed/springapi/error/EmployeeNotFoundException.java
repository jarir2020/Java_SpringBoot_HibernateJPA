package com.jarirahmed.springapi.error;

/** Raised when a requested employee does not exist. */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(long id) {
        super("Employee " + id + " was not found.");
    }
}
