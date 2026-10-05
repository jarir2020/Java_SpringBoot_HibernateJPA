package com.jarirahmed.hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Maps a project row on the other side of the assignment relationship. */
@Entity
@Table(name = "project")
public class HibernateProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal budget;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<HibernateEmployeeProject> assignments = new ArrayList<>();

    protected HibernateProject() {
        // Hibernate needs a no-argument constructor to materialize rows.
    }

    public HibernateProject(String name, BigDecimal budget) {
        this.name = name;
        this.budget = budget;
    }

    public void addAssignment(HibernateEmployeeProject assignment) {
        assignments.add(assignment);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public List<HibernateEmployeeProject> getAssignments() {
        return assignments;
    }
}
