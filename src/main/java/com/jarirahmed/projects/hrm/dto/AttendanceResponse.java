package com.jarirahmed.projects.hrm.dto;

import com.jarirahmed.projects.hrm.entity.AttendanceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AttendanceResponse(Long id, Long employeeId, String employeeName,
                                 LocalDate workDate, AttendanceStatus status, BigDecimal hours) {
}
