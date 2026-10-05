package com.jarirahmed.projects.hrm.error;

public class HrmNotFoundException extends RuntimeException {
    public HrmNotFoundException(String message) {
        super(message);
    }
}
