package com.jarirahmed.projects.hrm.dto;

import com.jarirahmed.projects.hrm.entity.NotificationStatus;

import java.time.Instant;

public record NotificationResponse(Long id, String recipient, String type, String message,
                                   NotificationStatus status, Instant createdAt) {
}
