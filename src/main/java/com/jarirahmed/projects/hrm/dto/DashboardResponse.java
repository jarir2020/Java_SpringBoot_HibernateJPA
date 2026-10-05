package com.jarirahmed.projects.hrm.dto;

public record DashboardResponse(
        CompanyResponse company,
        long employeeCount,
        long presentToday,
        long remoteToday,
        long pendingLeaveCount,
        long pendingNotificationCount,
        PayrollRunResponse latestPayroll,
        PayrollReportResponse latestReport) {
}
