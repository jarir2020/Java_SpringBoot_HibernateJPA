package com.jarirahmed.projects.employeecli;

/** Represents a malformed saved record rather than a temporary I/O failure. */
public class EmployeeStorageException extends RuntimeException {
    public EmployeeStorageException(String message) {
        super(message);
    }

    public EmployeeStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
