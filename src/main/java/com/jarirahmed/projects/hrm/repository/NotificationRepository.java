package com.jarirahmed.projects.hrm.repository;

import com.jarirahmed.projects.hrm.entity.Notification;
import com.jarirahmed.projects.hrm.entity.NotificationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NotificationRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Notification.class, id));
    }

    public Optional<Notification> findByReferenceKey(String referenceKey) {
        return entityManager.createQuery("select n from Notification n where n.referenceKey = :referenceKey", Notification.class)
                .setParameter("referenceKey", referenceKey)
                .getResultStream()
                .findFirst();
    }

    public List<Notification> findRecentByCompany(String companyCode) {
        return entityManager.createQuery("""
                select n from Notification n join fetch n.company c
                where c.code = :code order by n.createdAt desc
                """, Notification.class)
                .setParameter("code", companyCode)
                .setMaxResults(12)
                .getResultList();
    }

    public long countPendingByCompany(String companyCode) {
        return entityManager.createQuery("""
                select count(n) from Notification n join n.company c
                where c.code = :code and n.status = :status
                """, Long.class)
                .setParameter("code", companyCode)
                .setParameter("status", NotificationStatus.PENDING)
                .getSingleResult();
    }

    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            entityManager.persist(notification);
            return notification;
        }
        return entityManager.merge(notification);
    }
}
