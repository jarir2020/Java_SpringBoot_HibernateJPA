package com.jarirahmed.springapi.error;

/** A domain-level conflict that the HTTP layer maps to 409 Conflict. */
public class DuplicateEmployeeEmailException extends RuntimeException {
    public DuplicateEmployeeEmailException(String email) {
        super("An employee with email '" + email + "' already exists.");
    }
}
