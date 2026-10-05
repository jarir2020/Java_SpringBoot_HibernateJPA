package com.jarirahmed.projects.blog.error;

import java.util.Map;

/** Consistent JSON shape for browser-visible API failures. */
public record BlogApiError(int status, String message, Map<String, String> fieldErrors) {
}
