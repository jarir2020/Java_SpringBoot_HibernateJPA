package com.jarirahmed.springboot.model;

import java.util.List;

/** Public response used to prove that Boot loaded external configuration. */
public record BootInfoResponse(
        String applicationName,
        String courseName,
        String courseMode,
        List<String> activeProfiles) {
}
