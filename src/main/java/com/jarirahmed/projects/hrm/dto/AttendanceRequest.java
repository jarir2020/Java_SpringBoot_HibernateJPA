package com.jarirahmed.projects.hrm.dto;

import com.jarirahmed.projects.hrm.entity.AttendanceStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AttendanceRequest(
        @NotNull Long employeeId,
        @NotNull LocalDate workDate,
        @NotNull AttendanceStatus status,
        @NotNull @DecimalMin("0.00") @DecimalMax("24.00") BigDecimal hours) {
}
