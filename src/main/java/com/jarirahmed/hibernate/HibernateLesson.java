package com.jarirahmed.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.LazyInitializationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * Phase 6 uses Hibernate's native Session API. Spring Data JPA arrives later;
 * this class keeps the ORM bootstrap and transaction boundaries visible.
 */
public final class HibernateLesson {
    private HibernateLesson() {
    }

    public static void run() {
        System.out.println("\n=== PHASE 6: HIBERNATE ORM ===");
        System.out.println("ORM boundary: Java entity ↔ Hibernate Session ↔ JDBC ↔ H2");
        System.out.println("Database schema: generated from annotated entities (create-drop)");

        HibernateSqlCounter counter = new HibernateSqlCounter();
        try (SessionFactory sessionFactory = buildSessionFactory("phase6-lesson", counter)) {
            seed(sessionFactory);

            EmployeeSnapshot loaded = loadEmployee(sessionFactory, 1L);
            System.out.println("Loaded entity: " + loaded);
            System.out.println("First-level cache returns same object: "
                    + firstLevelCacheReturnsSameInstance(sessionFactory));
            System.out.println("Lazy department collection inside session: "
                    + employeeCountInDepartment(sessionFactory, 1L));
            System.out.println("Dirty checking updated salary: "
                    + demonstrateDirtyChecking(sessionFactory));
            System.out.println("Rollback preserved salary: "
                    + demonstrateRollback(sessionFactory));

            FetchObservation nPlusOne = demonstrateNPlusOne(sessionFactory, counter);
            FetchObservation joinFetch = demonstrateJoinFetch(sessionFactory, counter);
            System.out.println("N+1 department query count: " + nPlusOne.statementCount());
            System.out.println("Join-fetch query count: " + joinFetch.statementCount());
            System.out.println("Department names: " + joinFetch.departmentNames());
            System.out.println("Paginated employees: " + paginateEmployees(sessionFactory, 1, 2));
            System.out.println("Lazy collection after session close fails safely: "
                    + lazyCollectionRequiresSession(sessionFactory));
        }

        System.out.println("PHASE 6 COMPLETE");
    }

    /**
     * Builds one thread-safe SessionFactory for the application lifetime.
     * Individual units of work should open short-lived Sessions from it.
     */
    public static SessionFactory buildSessionFactory(
            String databaseName,
            HibernateSqlCounter counter) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.JAKARTA_JDBC_DRIVER, "org.h2.Driver")
                .applySetting(
                        AvailableSettings.JAKARTA_JDBC_URL,
                        "jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1")
                .applySetting(AvailableSettings.JAKARTA_JDBC_USER, "sa")
                .applySetting(AvailableSettings.JAKARTA_JDBC_PASSWORD, "")
                .applySetting(AvailableSettings.HBM2DDL_AUTO, "create-drop")
                .applySetting(AvailableSettings.SHOW_SQL, false)
                .applySetting(AvailableSettings.FORMAT_SQL, true)
                .applySetting(AvailableSettings.STATEMENT_INSPECTOR, counter)
                .build();

        try {
            return new MetadataSources(registry)
                    .addAnnotatedClass(HibernateDepartment.class)
                    .addAnnotatedClass(HibernateEmployee.class)
                    .addAnnotatedClass(HibernateProject.class)
                    .addAnnotatedClass(HibernateEmployeeProject.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (RuntimeException exception) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw exception;
        }
    }

    /** Seeds the same business relationships introduced in Phase 5. */
    public static void seed(SessionFactory sessionFactory) {
        inTransaction(sessionFactory, session -> {
            HibernateDepartment engineering = new HibernateDepartment("Engineering");
            HibernateDepartment operations = new HibernateDepartment("Operations");
            session.persist(engineering);
            session.persist(operations);

            HibernateProject platform = new HibernateProject(
                    "Learning Platform", new BigDecimal("50000.00"));
            HibernateProject crm = new HibernateProject(
                    "Internal CRM", new BigDecimal("75000.00"));
            session.persist(platform);
            session.persist(crm);

            HibernateEmployee alice = employee(
                    "Alice Rahman", "alice@example.com", "9000.00", "2024-01-15");
            HibernateEmployee bob = employee(
                    "Bob Karim", "bob@example.com", "6500.00", "2024-06-03");
            HibernateEmployee carol = employee(
                    "Carol Islam", "carol@example.com", "5200.00", "2025-02-10");
            engineering.addEmployee(alice);
            engineering.addEmployee(bob);
            operations.addEmployee(carol);
            session.persist(alice);
            session.persist(bob);
            session.persist(carol);
            session.flush();

            addAssignment(session, alice, platform, "Lead", "20.00", "2025-01-05");
            addAssignment(session, bob, platform, "Contributor", "15.00", "2025-01-12");
            addAssignment(session, bob, crm, "Analyst", "10.00", "2025-03-01");
            addAssignment(session, carol, crm, "Coordinator", "12.00", "2025-03-05");
            return null;
        });
    }

    public static EmployeeSnapshot loadEmployee(SessionFactory sessionFactory, long id) {
        return inTransaction(sessionFactory, session -> {
            HibernateEmployee employee = session.find(HibernateEmployee.class, id);
            if (employee == null) {
                throw new IllegalArgumentException("No employee exists with id " + id);
            }
            return new EmployeeSnapshot(
                    employee.getId(),
                    employee.getFullName(),
                    employee.getDisplayLabel(),
                    employee.getSalary());
        });
    }

    /** The same entity identity is reused inside one persistence context. */
    public static boolean firstLevelCacheReturnsSameInstance(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            HibernateEmployee first = session.find(HibernateEmployee.class, 1L);
            HibernateEmployee second = session.find(HibernateEmployee.class, 1L);
            return first == second;
        }
    }

    /** Accessing the lazy collection while the session is open initializes it. */
    public static int employeeCountInDepartment(SessionFactory sessionFactory, long id) {
        return inTransaction(sessionFactory, session ->
                session.find(HibernateDepartment.class, id).getEmployees().size());
    }

    /** Changes a managed field; Hibernate detects and writes it at commit time. */
    public static BigDecimal demonstrateDirtyChecking(SessionFactory sessionFactory) {
        return inTransaction(sessionFactory, session -> {
            HibernateEmployee employee = session.find(HibernateEmployee.class, 1L);
            employee.setSalary(new BigDecimal("9250.00"));
            return employee.getSalary();
        });
    }

    /** Rolls back a managed change and verifies it in a separate session. */
    public static BigDecimal demonstrateRollback(SessionFactory sessionFactory) {
        BigDecimal original = inTransaction(sessionFactory, session ->
                session.find(HibernateEmployee.class, 1L).getSalary());

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            HibernateEmployee employee = session.find(HibernateEmployee.class, 1L);
            employee.setSalary(new BigDecimal("1.00"));
            transaction.rollback();
        }

        BigDecimal afterRollback = inTransaction(sessionFactory, session ->
                session.find(HibernateEmployee.class, 1L).getSalary());
        if (!original.equals(afterRollback)) {
            throw new IllegalStateException("Rollback did not restore the managed entity state");
        }
        return afterRollback;
    }

    public static FetchObservation demonstrateNPlusOne(
            SessionFactory sessionFactory,
            HibernateSqlCounter counter) {
        counter.reset();
        List<String> departmentNames;
        try (Session session = sessionFactory.openSession()) {
            List<HibernateEmployee> employees = session.createSelectionQuery(
                            "from HibernateEmployee e order by e.id", HibernateEmployee.class)
                    .getResultList();
            departmentNames = employees.stream()
                    .map(employee -> employee.getDepartment().getName())
                    .toList();
        }
        return new FetchObservation(counter.count(), departmentNames);
    }

    public static FetchObservation demonstrateJoinFetch(
            SessionFactory sessionFactory,
            HibernateSqlCounter counter) {
        counter.reset();
        List<String> departmentNames;
        try (Session session = sessionFactory.openSession()) {
            List<HibernateEmployee> employees = session.createSelectionQuery(
                            "from HibernateEmployee e join fetch e.department order by e.id",
                            HibernateEmployee.class)
                    .getResultList();
            departmentNames = employees.stream()
                    .map(employee -> employee.getDepartment().getName())
                    .toList();
        }
        return new FetchObservation(counter.count(), departmentNames);
    }

    public static List<String> paginateEmployees(
            SessionFactory sessionFactory,
            int offset,
            int limit) {
        if (offset < 0 || limit < 1) {
            throw new IllegalArgumentException("Offset must be non-negative and limit must be positive");
        }
        return inTransaction(sessionFactory, session -> session.createSelectionQuery(
                        "from HibernateEmployee e order by e.id", HibernateEmployee.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList()
                .stream()
                .map(HibernateEmployee::getFullName)
                .toList());
    }

    /** A lazy collection cannot be initialized after its owning session closes. */
    public static boolean lazyCollectionRequiresSession(SessionFactory sessionFactory) {
        HibernateDepartment department;
        try (Session session = sessionFactory.openSession()) {
            department = session.find(HibernateDepartment.class, 1L);
        }

        try {
            department.getEmployees().size();
            return false;
        } catch (LazyInitializationException expected) {
            return true;
        }
    }

    public static <T> T inTransaction(
            SessionFactory sessionFactory,
            Function<Session, T> work) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                T result = work.apply(session);
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
            Session session,
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
        session.persist(assignment);
        employee.addAssignment(assignment);
        project.addAssignment(assignment);
    }

    public record EmployeeSnapshot(
            Long id,
            String name,
            String displayLabel,
            BigDecimal salary) {
    }

    public record FetchObservation(int statementCount, List<String> departmentNames) {
    }
}
