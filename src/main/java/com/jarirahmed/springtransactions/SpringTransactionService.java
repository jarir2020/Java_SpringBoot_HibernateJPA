package com.jarirahmed.springtransactions;

import com.jarirahmed.hibernate.HibernateDepartment;
import com.jarirahmed.hibernate.HibernateEmployee;
import com.jarirahmed.hibernate.HibernateEmployeeProject;
import com.jarirahmed.hibernate.HibernateEmployeeProjectId;
import com.jarirahmed.hibernate.HibernateProject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Service methods whose transaction boundaries are applied by Spring AOP. */
@Service
public class SpringTransactionService {
    private final NewTransactionService newTransactionService;

    @PersistenceContext
    private EntityManager entityManager;

    public SpringTransactionService(NewTransactionService newTransactionService) {
        this.newTransactionService = newTransactionService;
    }

    /** REQUIRED is the default: join an existing transaction or start one. */
    @Transactional
    public void seed() {
        HibernateDepartment engineering = new HibernateDepartment("Engineering");
        HibernateDepartment operations = new HibernateDepartment("Operations");
        entityManager.persist(engineering);
        entityManager.persist(operations);

        HibernateProject platform = new HibernateProject(
                "Learning Platform", new BigDecimal("50000.00"));
        HibernateProject crm = new HibernateProject(
                "Internal CRM", new BigDecimal("75000.00"));
        entityManager.persist(platform);
        entityManager.persist(crm);

        HibernateEmployee alice = employee(
                "Alice Rahman", "alice@example.com", "9000.00", "2024-01-15");
        HibernateEmployee bob = employee(
                "Bob Karim", "bob@example.com", "6500.00", "2024-06-03");
        HibernateEmployee carol = employee(
                "Carol Islam", "carol@example.com", "5200.00", "2025-02-10");
        engineering.addEmployee(alice);
        engineering.addEmployee(bob);
        operations.addEmployee(carol);
        entityManager.persist(alice);
        entityManager.persist(bob);
        entityManager.persist(carol);
        entityManager.flush();

        addAssignment(entityManager, alice, platform, "Lead", "20.00", "2025-01-05");
        addAssignment(entityManager, bob, platform, "Contributor", "15.00", "2025-01-12");
        addAssignment(entityManager, bob, crm, "Analyst", "10.00", "2025-03-01");
        addAssignment(entityManager, carol, crm, "Coordinator", "12.00", "2025-03-05");
    }

    @Transactional
    public long createEvent(String label) {
        TransactionEvent event = new TransactionEvent(label);
        entityManager.persist(event);
        entityManager.flush();
        return event.getId();
    }

    /** Runtime exceptions trigger rollback by Spring's default policy. */
    @Transactional
    public void createEventThenThrowRuntime(String label) {
        entityManager.persist(new TransactionEvent(label));
        throw new IllegalStateException("Outer transaction intentionally failed");
    }

    /** Checked exceptions need an explicit rollbackFor policy. */
    @Transactional(rollbackFor = CheckedTransactionFailure.class)
    public void createEventThenThrowChecked(String label) throws CheckedTransactionFailure {
        entityManager.persist(new TransactionEvent(label));
        throw new CheckedTransactionFailure("Checked transaction failure");
    }

    /** The outer transaction survives while the independent inner one rolls back. */
    @Transactional
    public void outerWithRequiresNewFailure() {
        entityManager.persist(new TransactionEvent("outer-before"));
        try {
            newTransactionService.createEventThenFail("inner-rolled-back");
        } catch (IllegalStateException expected) {
            // The inner REQUIRES_NEW transaction rolled back independently.
        }
        entityManager.persist(new TransactionEvent("outer-after"));
    }

    /** readOnly and isolation express the intended transaction semantics. */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<String> eventLabels() {
        return entityManager.createQuery(
                        "select e.label from TransactionEvent e order by e.id",
                        String.class)
                .getResultList();
    }

    /** Lazy navigation is safe because it happens inside the service transaction. */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public int engineeringEmployeeCount() {
        HibernateDepartment department = entityManager.find(HibernateDepartment.class, 1L);
        return department.getEmployees().size();
    }

    @Transactional(readOnly = true)
    public boolean transactionIsActive() {
        return entityManager.isJoinedToTransaction();
    }

    private static HibernateEmployee employee(
            String name,
            String email,
            String salary,
            String hiredOn) {
        return new HibernateEmployee(
                name,
                email,
                new BigDecimal(salary),
                LocalDate.parse(hiredOn));
    }

    private static void addAssignment(
            EntityManager entityManager,
            HibernateEmployee employee,
            HibernateProject project,
            String role,
            String hours,
            String assignedOn) {
        HibernateEmployeeProject assignment = new HibernateEmployeeProject(
                new HibernateEmployeeProjectId(employee.getId(), project.getId()),
                employee,
                project,
                role,
                new BigDecimal(hours),
                LocalDate.parse(assignedOn));
        entityManager.persist(assignment);
        employee.addAssignment(assignment);
        project.addAssignment(assignment);
    }
}
