package com.jarirahmed.springapi.dto;

/**
 * A small success envelope gives clients one predictable place for response
 * data and a human-readable operation message.
 */
public record ApiResponse<T>(
        String message,
        T data) {
}
