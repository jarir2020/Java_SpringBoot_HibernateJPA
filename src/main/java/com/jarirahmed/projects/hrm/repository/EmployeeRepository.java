package com.jarirahmed.projects.hrm.repository;

import com.jarirahmed.projects.hrm.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Employee> findActiveByCompanyCode(String companyCode) {
        return entityManager.createQuery("""
                select e from Employee e join fetch e.company c
                where c.code = :code and e.active = true order by e.fullName
                """, Employee.class)
                .setParameter("code", companyCode)
                .getResultList();
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Employee.class, id));
    }

    public Optional<Employee> findByEmail(String email) {
        return entityManager.createQuery("select e from Employee e join fetch e.company where lower(e.email) = lower(:email)", Employee.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public long countActiveByCompanyCode(String companyCode) {
        return entityManager.createQuery("""
                select count(e) from Employee e join e.company c
                where c.code = :code and e.active = true
                """, Long.class)
                .setParameter("code", companyCode)
                .getSingleResult();
    }

    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            entityManager.persist(employee);
            return employee;
        }
        return entityManager.merge(employee);
    }
}
