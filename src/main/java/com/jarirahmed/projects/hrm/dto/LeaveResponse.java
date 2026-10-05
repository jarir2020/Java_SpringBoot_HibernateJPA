package com.jarirahmed.projects.hrm.dto;

import com.jarirahmed.projects.hrm.entity.LeaveStatus;
import com.jarirahmed.projects.hrm.entity.LeaveType;

import java.time.LocalDate;

public record LeaveResponse(Long id, Long employeeId, String employeeName, LeaveType leaveType,
                            LocalDate startDate, LocalDate endDate, LeaveStatus status, String reason) {
}
