package com.jarirahmed.jpa;

import com.jarirahmed.hibernate.HibernateDepartment;
import com.jarirahmed.hibernate.HibernateEmployee;
import com.jarirahmed.hibernate.HibernateEmployeeProject;
import com.jarirahmed.hibernate.HibernateEmployeeProjectId;
import com.jarirahmed.hibernate.HibernateProject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.RollbackException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Phase 7 uses the standard Jakarta Persistence API while Hibernate remains
 * the provider underneath the persistence unit.
 */
public final class JpaLesson {
    private JpaLesson() {
    }

    public static void run() {
        System.out.println("\n=== PHASE 7: JPA ===");
        System.out.println("JPA is the API/specification; Hibernate is the provider");
        System.out.println("Application code: EntityManager ↔ persistence context ↔ database");

        try (EntityManagerFactory factory = buildEntityManagerFactory("phase7-lesson")) {
            seed(factory);

            System.out.println("JPQL high earners: " + jpqlHighEarners(factory, new BigDecimal("6000.00")));
            System.out.println("Criteria Engineering employees: "
                    + criteriaEmployeesInDepartment(factory, "Engineering"));
            System.out.println("EntityManager first-level cache returns same object: "
                    + firstLevelCacheReturnsSameInstance(factory));
            System.out.println("Lifecycle: " + demonstrateLifecycle(factory));
            System.out.println("Optimistic lock rejected stale update: "
                    + demonstrateOptimisticLocking(factory));
            System.out.println("Entity graph loaded department: "
                    + demonstrateEntityGraph(factory));
            System.out.println("JPA pagination: " + paginateEmployees(factory, 1, 2));
        }

        System.out.println("PHASE 7 COMPLETE");
    }

    /** Creates an application-managed EntityManagerFactory from persistence.xml. */
    public static EntityManagerFactory buildEntityManagerFactory(String databaseName) {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put(
                "jakarta.persistence.jdbc.url",
                "jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1");
        overrides.put("hibernate.hbm2ddl.auto", "create-drop");
        overrides.put("hibernate.show_sql", false);
        overrides.put("hibernate.format_sql", true);
        // Keep the JDBC connection until transaction completion so Spring's
        // HibernateJpaDialect can apply a requested isolation level.
        overrides.put(
                "hibernate.connection.handling_mode",
                "DELAYED_ACQUISITION_AND_HOLD");
        return Persistence.createEntityManagerFactory("phase7-unit", overrides);
    }

    public static void seed(EntityManagerFactory factory) {
        inTransaction(factory, entityManager -> {
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
            return null;
        });
    }

    /** JPQL is portable query text over entity names and fields. */
    public static List<String> jpqlHighEarners(
            EntityManagerFactory factory,
            BigDecimal minimumSalary) {
        return inTransaction(factory, entityManager -> {
            TypedQuery<HibernateEmployee> query = entityManager.createQuery(
                    "select e from HibernateEmployee e "
                            + "where e.salary >= :minimum order by e.salary desc",
                    HibernateEmployee.class);
            return query.setParameter("minimum", minimumSalary)
                    .getResultList()
                    .stream()
                    .map(HibernateEmployee::getFullName)
                    .toList();
        });
    }

    /** Criteria API builds a typed query tree instead of concatenating query text. */
    public static List<String> criteriaEmployeesInDepartment(
            EntityManagerFactory factory,
            String departmentName) {
        return inTransaction(factory, entityManager -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<HibernateEmployee> criteria =
                    builder.createQuery(HibernateEmployee.class);
            Root<HibernateEmployee> employee = criteria.from(HibernateEmployee.class);
            criteria.select(employee)
                    .where(builder.equal(
                            employee.get("department").get("name"), departmentName))
                    .orderBy(builder.asc(employee.get("id")));
            return entityManager.createQuery(criteria)
                    .getResultList()
                    .stream()
                    .map(HibernateEmployee::getFullName)
                    .toList();
        });
    }

    public static boolean firstLevelCacheReturnsSameInstance(EntityManagerFactory factory) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            HibernateEmployee first = entityManager.find(HibernateEmployee.class, 1L);
            HibernateEmployee second = entityManager.find(HibernateEmployee.class, 1L);
            return first == second;
        }
    }

    /** Demonstrates persist, clear/detach, merge, and EntityManager.contains. */
    public static LifecycleObservation demonstrateLifecycle(EntityManagerFactory factory) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            HibernateDepartment department = entityManager.find(HibernateDepartment.class, 1L);
            HibernateEmployee original = employee(
                    "Dina Ahmed", "dina@example.com", "4700.00", "2026-01-10");
            department.addEmployee(original);
            entityManager.persist(original);
            boolean managedAfterPersist = entityManager.contains(original);
            transaction.commit();

            entityManager.clear();
            boolean detachedAfterClear = !entityManager.contains(original);

            transaction.begin();
            HibernateEmployee merged = entityManager.merge(original);
            boolean mergeReturnedManagedCopy = entityManager.contains(merged)
                    && !entityManager.contains(original);
            transaction.rollback();

            return new LifecycleObservation(
                    original.getId(),
                    managedAfterPersist,
                    detachedAfterClear,
                    mergeReturnedManagedCopy);
        }
    }

    /**
     * Two EntityManagers read the same version. The stale writer is rejected
     * after the first writer commits an update.
     */
    public static OptimisticLockObservation demonstrateOptimisticLocking(
            EntityManagerFactory factory) {
        boolean staleUpdateRejected = false;
        try (EntityManager first = factory.createEntityManager();
             EntityManager second = factory.createEntityManager()) {
            EntityTransaction firstTransaction = first.getTransaction();
            EntityTransaction secondTransaction = second.getTransaction();
            firstTransaction.begin();
            secondTransaction.begin();

            HibernateEmployee current = first.find(HibernateEmployee.class, 1L);
            HibernateEmployee stale = second.find(HibernateEmployee.class, 1L);
            long versionBefore = stale.getVersion();

            current.setSalary(new BigDecimal("9100.00"));
            firstTransaction.commit();

            stale.setSalary(new BigDecimal("9200.00"));
            try {
                secondTransaction.commit();
            } catch (RollbackException exception) {
                staleUpdateRejected = hasCause(exception, OptimisticLockException.class);
                if (secondTransaction.isActive()) {
                    secondTransaction.rollback();
                }
            } catch (PersistenceException exception) {
                staleUpdateRejected = hasCause(exception, OptimisticLockException.class);
                if (secondTransaction.isActive()) {
                    secondTransaction.rollback();
                }
            }

            BigDecimal finalSalary = readSalary(factory, 1L);
            long finalVersion = readVersion(factory, 1L);
            return new OptimisticLockObservation(
                    staleUpdateRejected,
                    versionBefore,
                    finalVersion,
                    finalSalary);
        }
    }

    /** Entity graphs are a standard JPA fetch-plan mechanism. */
    public static EntityGraphObservation demonstrateEntityGraph(
            EntityManagerFactory factory) {
        return inTransaction(factory, entityManager -> {
            EntityGraph<HibernateEmployee> graph =
                    entityManager.createEntityGraph(HibernateEmployee.class);
            graph.addAttributeNodes("department");
            List<HibernateEmployee> employees = entityManager.createQuery(
                            "select e from HibernateEmployee e order by e.id",
                            HibernateEmployee.class)
                    .setHint("jakarta.persistence.fetchgraph", graph)
                    .getResultList();
            PersistenceUnitUtil persistence = factory.getPersistenceUnitUtil();
            return new EntityGraphObservation(
                    persistence.isLoaded(employees.getFirst(), "department"),
                    employees.stream()
                            .map(employee -> employee.getDepartment().getName())
                            .toList());
        });
    }

    public static List<String> paginateEmployees(
            EntityManagerFactory factory,
            int offset,
            int limit) {
        if (offset < 0 || limit < 1) {
            throw new IllegalArgumentException("Offset must be non-negative and limit must be positive");
        }
        return inTransaction(factory, entityManager -> entityManager.createQuery(
                        "select e from HibernateEmployee e order by e.id",
                        HibernateEmployee.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList()
                .stream()
                .map(HibernateEmployee::getFullName)
                .toList());
    }

    public static <T> T inTransaction(
            EntityManagerFactory factory,
            Function<EntityManager, T> work) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                T result = work.apply(entityManager);
                transaction.commit();
                return result;
            } catch (RuntimeException exception) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw exception;
            }
        }
    }

    private static BigDecimal readSalary(EntityManagerFactory factory, long id) {
        return inTransaction(factory, entityManager ->
                entityManager.find(HibernateEmployee.class, id).getSalary());
    }

    private static long readVersion(EntityManagerFactory factory, long id) {
        return inTransaction(factory, entityManager ->
                entityManager.find(HibernateEmployee.class, id).getVersion());
    }

    private static boolean hasCause(Throwable throwable, Class<? extends Throwable> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
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

    public record LifecycleObservation(
            Long id,
            boolean managedAfterPersist,
            boolean detachedAfterClear,
            boolean mergeReturnedManagedCopy) {
    }

    public record OptimisticLockObservation(
            boolean staleUpdateRejected,
            long versionBefore,
            long versionAfter,
            BigDecimal finalSalary) {
    }

    public record EntityGraphObservation(
            boolean departmentLoaded,
            List<String> departmentNames) {
    }
}
