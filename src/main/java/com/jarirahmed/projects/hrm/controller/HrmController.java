package com.jarirahmed.projects.hrm.controller;

import com.jarirahmed.projects.hrm.dto.AttendanceRequest;
import com.jarirahmed.projects.hrm.dto.AttendanceResponse;
import com.jarirahmed.projects.hrm.dto.AuditLogResponse;
import com.jarirahmed.projects.hrm.dto.CompanyResponse;
import com.jarirahmed.projects.hrm.dto.DashboardResponse;
import com.jarirahmed.projects.hrm.dto.EmployeeResponse;
import com.jarirahmed.projects.hrm.dto.JobResponse;
import com.jarirahmed.projects.hrm.dto.LeaveRequestInput;
import com.jarirahmed.projects.hrm.dto.LeaveResponse;
import com.jarirahmed.projects.hrm.dto.NotificationResponse;
import com.jarirahmed.projects.hrm.dto.PayrollReportResponse;
import com.jarirahmed.projects.hrm.dto.PayrollRunRequest;
import com.jarirahmed.projects.hrm.dto.PayrollRunResponse;
import com.jarirahmed.projects.hrm.service.HrmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/project5")
public class HrmController {
    private final HrmService hrmService;

    public HrmController(HrmService hrmService) {
        this.hrmService = hrmService;
    }

    @GetMapping("/companies")
    public List<CompanyResponse> companies() {
        return hrmService.companies();
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(@RequestParam(defaultValue = "NSTAR") String company) {
        return hrmService.dashboard(company);
    }

    @GetMapping("/employees")
    public List<EmployeeResponse> employees(@RequestParam(defaultValue = "NSTAR") String company) {
        return hrmService.employees(company);
    }

    @PostMapping("/attendance")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponse attendance(@Valid @RequestBody AttendanceRequest request,
                                         Authentication authentication) {
        return hrmService.recordAttendance(request, authentication.getName());
    }

    @PostMapping("/leave")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveResponse requestLeave(@Valid @RequestBody LeaveRequestInput request,
                                      Authentication authentication) {
        return hrmService.requestLeave(request, authentication.getName());
    }

    @PatchMapping("/leave/{leaveId}/approve")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public LeaveResponse approveLeave(@PathVariable Long leaveId, Authentication authentication) {
        return hrmService.approveLeave(leaveId, authentication.getName());
    }

    @PostMapping("/payroll/runs")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollRunResponse runPayroll(@Valid @RequestBody PayrollRunRequest request,
                                         Authentication authentication) {
        return hrmService.runPayroll(request, authentication.getName());
    }

    @GetMapping("/payroll/report")
    public PayrollReportResponse payrollReport(@RequestParam String company, @RequestParam String period) {
        return hrmService.payrollReport(company, period);
    }

    @GetMapping("/notifications")
    public List<NotificationResponse> notifications(@RequestParam(defaultValue = "NSTAR") String company) {
        return hrmService.notifications(company);
    }

    @GetMapping("/leave")
    public List<LeaveResponse> leaves(@RequestParam(defaultValue = "NSTAR") String company) {
        return hrmService.leaves(company);
    }

    @GetMapping("/audit")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public List<AuditLogResponse> audit(@RequestParam(defaultValue = "NSTAR") String company) {
        return hrmService.auditLogs(company);
    }

    @PostMapping("/jobs/leave-reminders")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public JobResponse queueLeaveReminders(Authentication authentication) {
        return hrmService.queueLeaveReminders(authentication.getName());
    }
}
