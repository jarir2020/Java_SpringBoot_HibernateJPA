package com.jarirahmed.projects.hrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Payroll values are snapshots so later salary edits do not rewrite history. */
@Entity
@Table(name = "hrm_payroll_entry")
public class PayrollEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payroll_run_id", nullable = false)
    private PayrollRun payrollRun;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deduction;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;

    protected PayrollEntry() {
    }

    public PayrollEntry(PayrollRun payrollRun, Employee employee, BigDecimal grossSalary,
                        BigDecimal deduction, BigDecimal netSalary) {
        this.payrollRun = payrollRun;
        this.employee = employee;
        this.grossSalary = grossSalary;
        this.deduction = deduction;
        this.netSalary = netSalary;
    }

    public Long getId() {
        return id;
    }

    public PayrollRun getPayrollRun() {
        return payrollRun;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public BigDecimal getDeduction() {
        return deduction;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }
}
