package com.jarirahmed.projects.hrm.error;

import java.time.Instant;
import java.util.Map;

public record HrmApiError(Instant timestamp, int status, String error, String message,
                          String path, Map<String, String> fieldErrors) {
}
