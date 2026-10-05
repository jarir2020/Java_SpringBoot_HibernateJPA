package com.jarirahmed.hibernate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/** Maps one department row and its lazily loaded employee collection. */
@Entity
@Table(name = "department")
public class HibernateDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @OneToMany(
            mappedBy = "department",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("id asc")
    private List<HibernateEmployee> employees = new ArrayList<>();

    protected HibernateDepartment() {
        // Hibernate needs a no-argument constructor to materialize rows.
    }

    public HibernateDepartment(String name) {
        this.name = name;
    }

    public void addEmployee(HibernateEmployee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<HibernateEmployee> getEmployees() {
        return employees;
    }
}
