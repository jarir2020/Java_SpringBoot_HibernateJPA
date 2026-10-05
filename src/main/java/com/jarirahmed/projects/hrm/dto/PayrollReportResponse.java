package com.jarirahmed.projects.hrm.dto;

import java.math.BigDecimal;

public record PayrollReportResponse(String companyCode, String period, BigDecimal totalGross,
                                    BigDecimal totalDeductions, BigDecimal totalNet,
                                    int employeeCount, boolean cacheHit) {
    public PayrollReportResponse fromCache() {
        return new PayrollReportResponse(companyCode, period, totalGross, totalDeductions,
                totalNet, employeeCount, true);
    }
}
