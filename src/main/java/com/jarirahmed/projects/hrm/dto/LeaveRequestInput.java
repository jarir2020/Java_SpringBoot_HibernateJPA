package com.jarirahmed.projects.hrm.dto;

import com.jarirahmed.projects.hrm.entity.LeaveType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LeaveRequestInput(
        @NotNull Long employeeId,
        @NotNull LeaveType leaveType,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotBlank String reason) {
}
