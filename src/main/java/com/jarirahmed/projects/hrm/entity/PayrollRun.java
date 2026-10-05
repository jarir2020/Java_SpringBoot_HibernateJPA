package com.jarirahmed.projects.hrm.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** A payroll run is a company-wide transaction with employee-level snapshots. */
@Entity
@Table(name = "hrm_payroll_run")
public class PayrollRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 7)
    private String period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollRunStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalGross;

    @Column(nullable = false)
    private Instant processedAt;

    @OneToMany(mappedBy = "payrollRun", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayrollEntry> entries = new ArrayList<>();

    protected PayrollRun() {
    }

    public PayrollRun(Company company, String period) {
        this.company = company;
        this.period = period;
        this.status = PayrollRunStatus.PROCESSED;
        this.totalGross = BigDecimal.ZERO;
        this.processedAt = Instant.now();
    }

    public void addEntry(PayrollEntry entry) {
        entries.add(entry);
        totalGross = totalGross.add(entry.getGrossSalary());
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getPeriod() {
        return period;
    }

    public PayrollRunStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalGross() {
        return totalGross;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public List<PayrollEntry> getEntries() {
        return List.copyOf(entries);
    }
}
