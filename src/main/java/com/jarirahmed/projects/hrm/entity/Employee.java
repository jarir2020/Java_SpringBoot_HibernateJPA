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
import java.time.LocalDate;

/** An employee is always owned by a company, which makes tenant filtering explicit. */
@Entity
@Table(name = "hrm_employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String employeeNumber;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 100)
    private String jobTitle;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlySalary;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    protected Employee() {
    }

    public Employee(String employeeNumber, String fullName, String email, String jobTitle,
                    BigDecimal monthlySalary, LocalDate hireDate, Company company) {
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.monthlySalary = monthlySalary;
        this.hireDate = hireDate;
        this.company = company;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public boolean isActive() {
        return active;
    }

    public Company getCompany() {
        return company;
    }
}
