package com.jarirahmed.projects.hrm.dto;

import com.jarirahmed.projects.hrm.entity.PayrollRunStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PayrollRunResponse(Long id, String companyCode, String period, PayrollRunStatus status,
                                 BigDecimal totalGross, BigDecimal totalDeductions,
                                 BigDecimal totalNet, int employeeCount, Instant processedAt) {
}
