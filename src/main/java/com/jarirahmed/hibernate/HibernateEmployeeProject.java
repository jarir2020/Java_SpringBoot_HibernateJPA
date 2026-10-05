package com.jarirahmed.hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Explicit many-to-many association entity. A direct @ManyToMany would lose
 * the role, hours, and assigned-on columns stored on this relationship.
 */
@Entity
@Table(name = "employee_project")
public class HibernateEmployeeProject {
    @EmbeddedId
    private HibernateEmployeeProjectId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id", nullable = false)
    private HibernateEmployee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private HibernateProject project;

    @Column(name = "assignment_role", nullable = false, length = 80)
    private String role;

    @Column(name = "hours_per_week", nullable = false, precision = 5, scale = 2)
    private BigDecimal hoursPerWeek;

    @Column(name = "assigned_on", nullable = false)
    private LocalDate assignedOn;

    protected HibernateEmployeeProject() {
        // Hibernate needs a no-argument constructor to materialize rows.
    }

    public HibernateEmployeeProject(
            HibernateEmployeeProjectId id,
            HibernateEmployee employee,
            HibernateProject project,
            String role,
            BigDecimal hoursPerWeek,
            LocalDate assignedOn) {
        this.id = id;
        this.employee = employee;
        this.project = project;
        this.role = role;
        this.hoursPerWeek = hoursPerWeek;
        this.assignedOn = assignedOn;
    }

    public HibernateEmployeeProjectId getId() {
        return id;
    }

    public HibernateEmployee getEmployee() {
        return employee;
    }

    public HibernateProject getProject() {
        return project;
    }

    public String getRole() {
        return role;
    }

    public BigDecimal getHoursPerWeek() {
        return hoursPerWeek;
    }

    public LocalDate getAssignedOn() {
        return assignedOn;
    }
}
