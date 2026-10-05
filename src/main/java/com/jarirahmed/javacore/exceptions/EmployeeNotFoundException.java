package com.jarirahmed.javacore.exceptions;

/** A checked exception makes the missing-employee contract explicit to callers. */
public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(int employeeId) {
        super("No employee exists with id " + employeeId + ".");
    }
}
