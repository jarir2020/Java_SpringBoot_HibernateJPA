package com.jarirahmed.projects.hrm.repository;

import com.jarirahmed.projects.hrm.entity.Company;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CompanyRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Company> findAll() {
        return entityManager.createQuery("select c from Company c where c.active = true order by c.name", Company.class)
                .getResultList();
    }

    public Optional<Company> findByCode(String code) {
        return entityManager.createQuery("select c from Company c where upper(c.code) = upper(:code)", Company.class)
                .setParameter("code", code)
                .getResultStream()
                .findFirst();
    }

    public long count() {
        return entityManager.createQuery("select count(c) from Company c", Long.class).getSingleResult();
    }

    public Company save(Company company) {
        if (company.getId() == null) {
            entityManager.persist(company);
            return company;
        }
        return entityManager.merge(company);
    }
}
