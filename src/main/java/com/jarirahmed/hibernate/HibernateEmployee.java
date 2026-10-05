package com.jarirahmed.hibernate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Maps an employee row and demonstrates a lazy many-to-one relationship. */
@Entity
@Table(name = "employee")
public class HibernateEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private HibernateDepartment department;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "hired_on", nullable = false)
    private LocalDate hiredOn;

    @Version
    @Column(name = "entity_version", nullable = false)
    private long version;

    @OneToMany(
            mappedBy = "employee",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<HibernateEmployeeProject> assignments = new ArrayList<>();

    @Transient
    private String displayLabel;

    protected HibernateEmployee() {
        // Hibernate needs a no-argument constructor to materialize rows.
    }

    public HibernateEmployee(
            String fullName,
            String email,
            BigDecimal salary,
            LocalDate hiredOn) {
        this.fullName = fullName;
        this.email = email;
        this.salary = salary;
        this.hiredOn = hiredOn;
    }

    public void setDepartment(HibernateDepartment department) {
        this.department = department;
    }

    public void addAssignment(HibernateEmployeeProject assignment) {
        assignments.add(assignment);
    }

    public Long getId() {
        return id;
    }

    public HibernateDepartment getDepartment() {
        return department;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getHiredOn() {
        return hiredOn;
    }

    public long getVersion() {
        return version;
    }

    public List<HibernateEmployeeProject> getAssignments() {
        return assignments;
    }

    public String getDisplayLabel() {
        if (displayLabel == null) {
            displayLabel = fullName + " <" + email + ">";
        }
        return displayLabel;
    }
}
