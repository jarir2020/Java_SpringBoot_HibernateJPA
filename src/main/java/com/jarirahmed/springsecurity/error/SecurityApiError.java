package com.jarirahmed.springsecurity.error;

/** Stable JSON shape returned before a request reaches an MVC controller. */
public record SecurityApiError(
        int status,
        String error,
        String message,
        String path) {
}
