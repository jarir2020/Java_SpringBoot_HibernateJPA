package com.jarirahmed.projects.hrm.dto;

import java.time.Instant;

public record AuditLogResponse(Long id, String actor, String action, String resourceType,
                               String resourceId, String details, Instant createdAt) {
}
