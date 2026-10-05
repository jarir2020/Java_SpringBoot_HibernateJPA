package com.jarirahmed.springmvc.error;

import java.util.Map;

/** Stable public error shape; internal exception details are not exposed. */
public record ApiError(
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {
}
