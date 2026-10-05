package com.jarirahmed.projects.hrm.repository;

import com.jarirahmed.projects.hrm.entity.LeaveRequest;
import com.jarirahmed.projects.hrm.entity.LeaveStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LeaveRequestRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public long countByCompanyAndStatus(String companyCode, LeaveStatus status) {
        return entityManager.createQuery("""
                select count(l) from LeaveRequest l join l.employee e join e.company c
                where c.code = :code and l.status = :status
                """, Long.class)
                .setParameter("code", companyCode)
                .setParameter("status", status)
                .getSingleResult();
    }

    public List<LeaveRequest> findPending() {
        return entityManager.createQuery("""
                select l from LeaveRequest l join fetch l.employee e join fetch e.company c
                where l.status = :status order by l.requestedAt
                """, LeaveRequest.class)
                .setParameter("status", LeaveStatus.PENDING)
                .getResultList();
    }

    public List<LeaveRequest> findRecentByCompany(String companyCode) {
        return entityManager.createQuery("""
                select l from LeaveRequest l join fetch l.employee e join fetch e.company c
                where c.code = :code order by l.requestedAt desc
                """, LeaveRequest.class)
                .setParameter("code", companyCode)
                .setMaxResults(10)
                .getResultList();
    }

    public Optional<LeaveRequest> findById(Long id) {
        return entityManager.createQuery("""
                select l from LeaveRequest l join fetch l.employee e join fetch e.company
                where l.id = :id
                """, LeaveRequest.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public LeaveRequest save(LeaveRequest leaveRequest) {
        if (leaveRequest.getId() == null) {
            entityManager.persist(leaveRequest);
            return leaveRequest;
        }
        return entityManager.merge(leaveRequest);
    }
}
