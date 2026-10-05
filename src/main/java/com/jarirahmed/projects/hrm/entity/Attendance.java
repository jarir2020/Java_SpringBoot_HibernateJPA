package com.jarirahmed.projects.hrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One daily attendance record demonstrates an employee-to-event relationship. */
@Entity
@Table(name = "hrm_attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "work_date"}))
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal hours;

    protected Attendance() {
    }

    public Attendance(Employee employee, LocalDate workDate, AttendanceStatus status, BigDecimal hours) {
        this.employee = employee;
        this.workDate = workDate;
        this.status = status;
        this.hours = hours;
    }

    public void update(AttendanceStatus status, BigDecimal hours) {
        this.status = status;
        this.hours = hours;
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public BigDecimal getHours() {
        return hours;
    }
}
