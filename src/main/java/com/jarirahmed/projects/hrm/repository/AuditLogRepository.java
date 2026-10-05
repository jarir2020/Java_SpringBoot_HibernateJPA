package com.jarirahmed.projects.hrm.repository;

import com.jarirahmed.projects.hrm.entity.AuditLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<AuditLog> findRecentByCompany(String companyCode) {
        return entityManager.createQuery("""
                select a from AuditLog a join fetch a.company c
                where c.code = :code order by a.createdAt desc
                """, AuditLog.class)
                .setParameter("code", companyCode)
                .setMaxResults(12)
                .getResultList();
    }

    public AuditLog save(AuditLog auditLog) {
        if (auditLog.getId() == null) {
            entityManager.persist(auditLog);
            return auditLog;
        }
        return entityManager.merge(auditLog);
    }
}
