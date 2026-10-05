package com.jarirahmed.projects.hrm.repository;

import com.jarirahmed.projects.hrm.entity.Attendance;
import com.jarirahmed.projects.hrm.entity.AttendanceStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class AttendanceRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public long countByCompanyAndDateAndStatus(String companyCode, LocalDate date, AttendanceStatus status) {
        return entityManager.createQuery("""
                select count(a) from Attendance a join a.employee e join e.company c
                where c.code = :code and a.workDate = :date and a.status = :status
                """, Long.class)
                .setParameter("code", companyCode)
                .setParameter("date", date)
                .setParameter("status", status)
                .getSingleResult();
    }

    public Optional<Attendance> findByEmployeeAndDate(Long employeeId, LocalDate date) {
        return entityManager.createQuery("""
                select a from Attendance a where a.employee.id = :employeeId and a.workDate = :date
                """, Attendance.class)
                .setParameter("employeeId", employeeId)
                .setParameter("date", date)
                .getResultStream()
                .findFirst();
    }

    public Attendance save(Attendance attendance) {
        if (attendance.getId() == null) {
            entityManager.persist(attendance);
            return attendance;
        }
        return entityManager.merge(attendance);
    }
}
