package com.jarirahmed.springcore.service;

import org.springframework.stereotype.Component;

/** A small component that Spring discovers through component scanning. */
@Component
public class GreetingFormatter {
    public String normalize(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return "Anonymous";
        }
        return rawName.trim();
    }
}
