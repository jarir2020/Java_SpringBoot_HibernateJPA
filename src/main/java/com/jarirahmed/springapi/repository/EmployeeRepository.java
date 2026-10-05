package com.jarirahmed.springapi.repository;

import com.jarirahmed.hibernate.HibernateDepartment;
import com.jarirahmed.hibernate.HibernateEmployee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Persistence layer: JPQL and EntityManager details stop here. The service
 * should not need to know how an employee is queried or counted.
 */
@Repository
public class EmployeeRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public HibernateEmployee save(HibernateEmployee employee) {
        entityManager.persist(employee);
        return employee;
    }

    public Optional<HibernateEmployee> findById(long id) {
        return Optional.ofNullable(entityManager.find(HibernateEmployee.class, id));
    }

    public Optional<HibernateDepartment> findDepartmentByName(String name) {
        List<HibernateDepartment> departments = entityManager.createQuery(
                        "select d from HibernateDepartment d "
                                + "where lower(d.name) = lower(:name)",
                        HibernateDepartment.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();
        return departments.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "select count(e) from HibernateEmployee e "
                                + "where lower(e.email) = lower(:email)",
                        Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    public List<HibernateEmployee> findPage(String departmentName, int page, int size) {
        StringBuilder jpql = new StringBuilder(
                "select e from HibernateEmployee e join fetch e.department");
        if (departmentName != null && !departmentName.isBlank()) {
            jpql.append(" where lower(e.department.name) = lower(:departmentName)");
        }
        jpql.append(" order by e.id asc");

        var query = entityManager.createQuery(jpql.toString(), HibernateEmployee.class)
                .setFirstResult(page * size)
                .setMaxResults(size);
        if (departmentName != null && !departmentName.isBlank()) {
            query.setParameter("departmentName", departmentName.trim());
        }
        return query.getResultList();
    }

    public long count(String departmentName) {
        String jpql = "select count(e) from HibernateEmployee e";
        if (departmentName != null && !departmentName.isBlank()) {
            jpql += " where lower(e.department.name) = lower(:departmentName)";
        }

        var query = entityManager.createQuery(jpql, Long.class);
        if (departmentName != null && !departmentName.isBlank()) {
            query.setParameter("departmentName", departmentName.trim());
        }
        return query.getSingleResult();
    }

    public void flush() {
        entityManager.flush();
    }
}
