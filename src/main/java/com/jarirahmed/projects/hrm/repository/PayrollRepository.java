package com.jarirahmed.projects.hrm.repository;

import com.jarirahmed.projects.hrm.entity.PayrollRun;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PayrollRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<PayrollRun> findByCompanyAndPeriod(String companyCode, String period) {
        return entityManager.createQuery("""
                select distinct p from PayrollRun p join fetch p.company c
                left join fetch p.entries entries left join fetch entries.employee
                where c.code = :code and p.period = :period
                """, PayrollRun.class)
                .setParameter("code", companyCode)
                .setParameter("period", period)
                .getResultStream()
                .findFirst();
    }

    public Optional<PayrollRun> findLatestByCompany(String companyCode) {
        return entityManager.createQuery("""
                select distinct p from PayrollRun p join fetch p.company c
                left join fetch p.entries entries left join fetch entries.employee
                where c.code = :code order by p.period desc
                """, PayrollRun.class)
                .setParameter("code", companyCode)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public List<PayrollRun> findRecentByCompany(String companyCode) {
        return entityManager.createQuery("""
                select distinct p from PayrollRun p join fetch p.company c
                left join fetch p.entries entries left join fetch entries.employee
                where c.code = :code order by p.period desc
                """, PayrollRun.class)
                .setParameter("code", companyCode)
                .setMaxResults(6)
                .getResultList();
    }

    public PayrollRun save(PayrollRun payrollRun) {
        if (payrollRun.getId() == null) {
            entityManager.persist(payrollRun);
            return payrollRun;
        }
        return entityManager.merge(payrollRun);
    }
}
