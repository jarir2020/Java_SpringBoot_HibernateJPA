package com.jarirahmed.projects.hrm.service;

import com.jarirahmed.projects.hrm.cache.ReportCache;
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
import com.jarirahmed.projects.hrm.entity.Attendance;
import com.jarirahmed.projects.hrm.entity.Company;
import com.jarirahmed.projects.hrm.entity.Employee;
import com.jarirahmed.projects.hrm.entity.LeaveRequest;
import com.jarirahmed.projects.hrm.entity.LeaveStatus;
import com.jarirahmed.projects.hrm.entity.Notification;
import com.jarirahmed.projects.hrm.entity.PayrollEntry;
import com.jarirahmed.projects.hrm.entity.PayrollRun;
import com.jarirahmed.projects.hrm.error.HrmConflictException;
import com.jarirahmed.projects.hrm.error.HrmNotFoundException;
import com.jarirahmed.projects.hrm.repository.AttendanceRepository;
import com.jarirahmed.projects.hrm.repository.AuditLogRepository;
import com.jarirahmed.projects.hrm.repository.CompanyRepository;
import com.jarirahmed.projects.hrm.repository.EmployeeRepository;
import com.jarirahmed.projects.hrm.repository.LeaveRequestRepository;
import com.jarirahmed.projects.hrm.repository.NotificationRepository;
import com.jarirahmed.projects.hrm.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

/** Application use cases: controllers stay thin while transactions remain visible here. */
@Service
public class HrmService {
    private static final BigDecimal DEDUCTION_RATE = new BigDecimal("0.10");

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReportCache reportCache;
    private final NotificationDispatcher notificationDispatcher;

    public HrmService(CompanyRepository companyRepository, EmployeeRepository employeeRepository,
                      AttendanceRepository attendanceRepository, LeaveRequestRepository leaveRequestRepository,
                      PayrollRepository payrollRepository, NotificationRepository notificationRepository,
                      AuditLogRepository auditLogRepository, ReportCache reportCache,
                      NotificationDispatcher notificationDispatcher) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.payrollRepository = payrollRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
        this.reportCache = reportCache;
        this.notificationDispatcher = notificationDispatcher;
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> companies() {
        return companyRepository.findAll().stream()
                .map(company -> new CompanyResponse(company.getCode(), company.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> employees(String companyCode) {
        company(companyCode);
        return employeeRepository.findActiveByCompanyCode(normalize(companyCode)).stream()
                .map(this::employeeResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard(String companyCode) {
        Company company = company(companyCode);
        String code = company.getCode();
        LocalDate today = LocalDate.now();
        PayrollRun latest = payrollRepository.findLatestByCompany(code).orElse(null);
        PayrollRunResponse latestResponse = latest == null ? null : payrollResponse(latest);
        PayrollReportResponse report = latest == null ? null : reportFor(latest);
        return new DashboardResponse(
                new CompanyResponse(company.getCode(), company.getName()),
                employeeRepository.countActiveByCompanyCode(code),
                attendanceRepository.countByCompanyAndDateAndStatus(code, today,
                        com.jarirahmed.projects.hrm.entity.AttendanceStatus.PRESENT),
                attendanceRepository.countByCompanyAndDateAndStatus(code, today,
                        com.jarirahmed.projects.hrm.entity.AttendanceStatus.REMOTE),
                leaveRequestRepository.countByCompanyAndStatus(code, LeaveStatus.PENDING),
                notificationRepository.countPendingByCompany(code),
                latestResponse,
                report);
    }

    @Transactional
    public AttendanceResponse recordAttendance(AttendanceRequest request, String actor) {
        Employee employee = employee(request.employeeId());
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee.getId(), request.workDate())
                .map(existing -> {
                    existing.update(request.status(), request.hours());
                    return existing;
                })
                .orElseGet(() -> attendanceRepository.save(new Attendance(employee, request.workDate(),
                        request.status(), request.hours())));
        auditLogRepository.save(new com.jarirahmed.projects.hrm.entity.AuditLog(employee.getCompany(), actor,
                "ATTENDANCE_RECORDED", "Attendance", attendance.getId().toString(),
                employee.getFullName() + " marked " + request.status()));
        return new AttendanceResponse(attendance.getId(), employee.getId(), employee.getFullName(),
                attendance.getWorkDate(), attendance.getStatus(), attendance.getHours());
    }

    @Transactional
    public LeaveResponse requestLeave(LeaveRequestInput request, String actor) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new HrmConflictException("Leave end date cannot be before its start date.");
        }
        Employee employee = employee(request.employeeId());
        LeaveRequest leave = leaveRequestRepository.save(new LeaveRequest(employee, request.leaveType(),
                request.startDate(), request.endDate(), request.reason()));
        auditLogRepository.save(new com.jarirahmed.projects.hrm.entity.AuditLog(employee.getCompany(), actor,
                "LEAVE_REQUESTED", "LeaveRequest", leave.getId().toString(),
                employee.getFullName() + " requested " + request.leaveType()));
        return leaveResponse(leave);
    }

    @Transactional
    public LeaveResponse approveLeave(Long leaveId, String actor) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new HrmNotFoundException("Leave request " + leaveId + " was not found."));
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new HrmConflictException("Only pending leave requests can be approved.");
        }
        leave.approve();
        leaveRequestRepository.save(leave);
        Company company = leave.getEmployee().getCompany();
        auditLogRepository.save(new com.jarirahmed.projects.hrm.entity.AuditLog(company, actor,
                "LEAVE_APPROVED", "LeaveRequest", leave.getId().toString(),
                "Approved leave for " + leave.getEmployee().getFullName()));
        Notification notification = notificationRepository.save(new Notification(company,
                leave.getEmployee().getEmail(), "LEAVE_APPROVED",
                "Your leave request was approved.", "leave-approved:" + leave.getId()));
        notificationDispatcher.queueAfterCommit(notification.getId());
        return leaveResponse(leave);
    }

    @Transactional
    public PayrollRunResponse runPayroll(PayrollRunRequest request, String actor) {
        YearMonth.parse(request.period());
        Company company = company(request.companyCode());
        String code = company.getCode();
        PayrollRun existing = payrollRepository.findByCompanyAndPeriod(code, request.period()).orElse(null);
        if (existing != null) {
            return payrollResponse(existing);
        }

        PayrollRun run = new PayrollRun(company, request.period());
        for (Employee employee : employeeRepository.findActiveByCompanyCode(code)) {
            BigDecimal gross = employee.getMonthlySalary().setScale(2, RoundingMode.HALF_UP);
            BigDecimal deduction = gross.multiply(DEDUCTION_RATE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal net = gross.subtract(deduction).setScale(2, RoundingMode.HALF_UP);
            run.addEntry(new PayrollEntry(run, employee, gross, deduction, net));
        }
        payrollRepository.save(run);
        auditLogRepository.save(new com.jarirahmed.projects.hrm.entity.AuditLog(company, actor,
                "PAYROLL_PROCESSED", "PayrollRun", run.getPeriod(),
                "Processed payroll for " + run.getEntries().size() + " employees."));
        Notification notification = notificationRepository.save(new Notification(company,
                "hr@" + code.toLowerCase(Locale.ROOT) + ".example", "PAYROLL_PROCESSED",
                "Payroll " + run.getPeriod() + " was processed.", "payroll:" + code + ":" + run.getPeriod()));
        notificationDispatcher.queueAfterCommit(notification.getId());
        reportCache.evict(reportKey(code, run.getPeriod()));
        return payrollResponse(run);
    }

    @Transactional(readOnly = true)
    public PayrollReportResponse payrollReport(String companyCode, String period) {
        String code = company(companyCode).getCode();
        YearMonth.parse(period);
        PayrollRun run = payrollRepository.findByCompanyAndPeriod(code, period)
                .orElseThrow(() -> new HrmNotFoundException("No payroll run exists for " + code + " and " + period + "."));
        return reportFor(run);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> notifications(String companyCode) {
        company(companyCode);
        return notificationRepository.findRecentByCompany(normalize(companyCode)).stream()
                .map(notification -> new NotificationResponse(notification.getId(), notification.getRecipient(),
                        notification.getType(), notification.getMessage(), notification.getStatus(), notification.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> leaves(String companyCode) {
        company(companyCode);
        return leaveRequestRepository.findRecentByCompany(normalize(companyCode)).stream()
                .map(this::leaveResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> auditLogs(String companyCode) {
        company(companyCode);
        return auditLogRepository.findRecentByCompany(normalize(companyCode)).stream()
                .map(audit -> new AuditLogResponse(audit.getId(), audit.getActor(), audit.getAction(),
                        audit.getResourceType(), audit.getResourceId(), audit.getDetails(), audit.getCreatedAt()))
                .toList();
    }

    @Transactional
    public JobResponse queueLeaveReminders(String actor) {
        int queued = 0;
        for (LeaveRequest leave : leaveRequestRepository.findPending()) {
            String reference = "leave-reminder:" + leave.getId();
            if (notificationRepository.findByReferenceKey(reference).isEmpty()) {
                Notification notification = notificationRepository.save(new Notification(
                        leave.getEmployee().getCompany(), "hr@" + leave.getEmployee().getCompany().getCode().toLowerCase(Locale.ROOT)
                        + ".example", "LEAVE_REMINDER", "A leave request is waiting for review.", reference));
                notificationDispatcher.queueAfterCommit(notification.getId());
                auditLogRepository.save(new com.jarirahmed.projects.hrm.entity.AuditLog(
                        leave.getEmployee().getCompany(), actor, "LEAVE_REMINDER_QUEUED", "LeaveRequest",
                        leave.getId().toString(), "Background reminder queued."));
                queued++;
            }
        }
        return new JobResponse("leave-reminders", queued, "Pending leave reminders were queued for background delivery.");
    }

    private PayrollReportResponse reportFor(PayrollRun run) {
        String key = reportKey(run.getCompany().getCode(), run.getPeriod());
        var cached = reportCache.get(key);
        if (cached.isPresent()) {
            return cached.get().fromCache();
        }
        BigDecimal deductions = run.getEntries().stream()
                .map(PayrollEntry::getDeduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal net = run.getEntries().stream()
                .map(PayrollEntry::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PayrollReportResponse report = new PayrollReportResponse(run.getCompany().getCode(), run.getPeriod(),
                run.getTotalGross(), deductions, net, run.getEntries().size(), false);
        reportCache.put(key, report);
        return report;
    }

    private PayrollRunResponse payrollResponse(PayrollRun run) {
        BigDecimal deductions = run.getEntries().stream()
                .map(PayrollEntry::getDeduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal net = run.getEntries().stream()
                .map(PayrollEntry::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PayrollRunResponse(run.getId(), run.getCompany().getCode(), run.getPeriod(), run.getStatus(),
                run.getTotalGross(), deductions, net, run.getEntries().size(), run.getProcessedAt());
    }

    private EmployeeResponse employeeResponse(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getEmployeeNumber(), employee.getFullName(),
                employee.getEmail(), employee.getJobTitle(), employee.getMonthlySalary(), employee.getHireDate(),
                employee.getCompany().getCode(), employee.isActive());
    }

    private LeaveResponse leaveResponse(LeaveRequest leave) {
        return new LeaveResponse(leave.getId(), leave.getEmployee().getId(), leave.getEmployee().getFullName(),
                leave.getLeaveType(), leave.getStartDate(), leave.getEndDate(), leave.getStatus(), leave.getReason());
    }

    private Employee employee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new HrmNotFoundException("Employee " + id + " was not found."));
    }

    private Company company(String code) {
        return companyRepository.findByCode(normalize(code))
                .orElseThrow(() -> new HrmNotFoundException("Company " + code + " was not found."));
    }

    private String normalize(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }

    private String reportKey(String companyCode, String period) {
        return "hrm:payroll-report:" + companyCode + ":" + period;
    }
}
