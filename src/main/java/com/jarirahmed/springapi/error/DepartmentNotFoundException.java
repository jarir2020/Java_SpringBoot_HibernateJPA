package com.jarirahmed.springapi.error;

/** Raised when a request refers to a department outside the current domain. */
public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(String departmentName) {
        super("Department '" + departmentName + "' was not found.");
    }
}
