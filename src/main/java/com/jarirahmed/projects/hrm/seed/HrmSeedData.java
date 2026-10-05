package com.jarirahmed.projects.hrm.seed;

import com.jarirahmed.projects.hrm.entity.Attendance;
import com.jarirahmed.projects.hrm.entity.AttendanceStatus;
import com.jarirahmed.projects.hrm.entity.AuditLog;
import com.jarirahmed.projects.hrm.entity.Company;
import com.jarirahmed.projects.hrm.entity.Employee;
import com.jarirahmed.projects.hrm.entity.LeaveRequest;
import com.jarirahmed.projects.hrm.entity.LeaveType;
import com.jarirahmed.projects.hrm.entity.PayrollEntry;
import com.jarirahmed.projects.hrm.entity.PayrollRun;
import com.jarirahmed.projects.hrm.repository.AttendanceRepository;
import com.jarirahmed.projects.hrm.repository.AuditLogRepository;
import com.jarirahmed.projects.hrm.repository.CompanyRepository;
import com.jarirahmed.projects.hrm.repository.EmployeeRepository;
import com.jarirahmed.projects.hrm.repository.LeaveRequestRepository;
import com.jarirahmed.projects.hrm.repository.PayrollRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

/** Disposable H2 fixtures give the browser dashboard something meaningful to explore. */
@Component
public class HrmSeedData {
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;
    private final AuditLogRepository auditLogRepository;

    public HrmSeedData(CompanyRepository companyRepository, EmployeeRepository employeeRepository,
                       AttendanceRepository attendanceRepository, LeaveRequestRepository leaveRequestRepository,
                       PayrollRepository payrollRepository, AuditLogRepository auditLogRepository) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.payrollRepository = payrollRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void seed() {
        if (companyRepository.count() > 0) {
            return;
        }

        Company northstar = companyRepository.save(new Company("NSTAR", "Northstar Labs"));
        Company banyan = companyRepository.save(new Company("BANYAN", "Banyan Foods"));

        Employee alice = employeeRepository.save(new Employee("NSTAR-001", "Alice Rahman",
                "alice@northstar.example", "Software Engineer", new BigDecimal("9000.00"),
                LocalDate.of(2024, 1, 15), northstar));
        Employee bob = employeeRepository.save(new Employee("NSTAR-002", "Bob Karim",
                "bob@northstar.example", "Product Manager", new BigDecimal("7000.00"),
                LocalDate.of(2024, 6, 3), northstar));
        Employee carol = employeeRepository.save(new Employee("NSTAR-003", "Carol Islam",
                "carol@northstar.example", "Product Designer", new BigDecimal("6200.00"),
                LocalDate.of(2025, 2, 10), northstar));
        Employee maya = employeeRepository.save(new Employee("BANYAN-001", "Maya Noor",
                "maya@banyan.example", "Operations Lead", new BigDecimal("6800.00"),
                LocalDate.of(2023, 9, 18), banyan));

        LocalDate today = LocalDate.now();
        attendanceRepository.save(new Attendance(alice, today, AttendanceStatus.PRESENT, new BigDecimal("8.00")));
        attendanceRepository.save(new Attendance(bob, today, AttendanceStatus.REMOTE, new BigDecimal("8.00")));
        attendanceRepository.save(new Attendance(carol, today.minusDays(1), AttendanceStatus.PRESENT, new BigDecimal("7.50")));
        attendanceRepository.save(new Attendance(maya, today, AttendanceStatus.LATE, new BigDecimal("6.50")));

        leaveRequestRepository.save(new LeaveRequest(carol, LeaveType.ANNUAL, today.plusDays(4),
                today.plusDays(6), "Family travel and rest."));
        LeaveRequest approved = new LeaveRequest(bob, LeaveType.PERSONAL, today.minusDays(10),
                today.minusDays(9), "Personal appointment.");
        approved.approve();
        leaveRequestRepository.save(approved);

        PayrollRun run = new PayrollRun(northstar, YearMonth.now().minusMonths(1).toString());
        addEntry(run, alice);
        addEntry(run, bob);
        addEntry(run, carol);
        payrollRepository.save(run);

        auditLogRepository.save(new AuditLog(northstar, "seed", "PAYROLL_PROCESSED", "PayrollRun",
                run.getPeriod(), "Seeded the previous monthly payroll run."));
        auditLogRepository.save(new AuditLog(northstar, "seed", "LEAVE_REQUESTED", "LeaveRequest",
                "pending-demo", "Seeded a pending leave request for the dashboard."));
    }

    private void addEntry(PayrollRun run, Employee employee) {
        BigDecimal gross = employee.getMonthlySalary();
        BigDecimal deduction = gross.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        run.addEntry(new PayrollEntry(run, employee, gross, deduction, gross.subtract(deduction)));
    }
}
