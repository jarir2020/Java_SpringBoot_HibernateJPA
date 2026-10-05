package com.jarirahmed.springmvc.model;

/** Internal model for the in-memory MVC lesson. A database arrives later. */
public record Post(
        int id,
        String title,
        String body,
        String authorEmail,
        String category,
        boolean published) {
}
