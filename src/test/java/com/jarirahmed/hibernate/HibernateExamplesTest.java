package com.jarirahmed.hibernate;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies native Hibernate behavior without Spring Data or EntityManager. */
class HibernateExamplesTest {
    @Test
    void annotated_entities_are_bootstrapped_and_seeded() {
        HibernateSqlCounter counter = new HibernateSqlCounter();
        try (SessionFactory sessionFactory = HibernateLesson.buildSessionFactory(
                "phase6-mapping-test", counter)) {
            HibernateLesson.seed(sessionFactory);

            HibernateLesson.EmployeeSnapshot employee =
                    HibernateLesson.loadEmployee(sessionFactory, 1L);
            assertEquals("Alice Rahman", employee.name());
            assertEquals("Alice Rahman <alice@example.com>", employee.displayLabel());
            assertEquals(new BigDecimal("9000.00"), employee.salary());
            assertEquals(2, HibernateLesson.employeeCountInDepartment(sessionFactory, 1L));
        }
    }

    @Test
    void persistence_context_dirty_checking_and_rollback_are_visible() {
        HibernateSqlCounter counter = new HibernateSqlCounter();
        try (SessionFactory sessionFactory = HibernateLesson.buildSessionFactory(
                "phase6-lifecycle-test", counter)) {
            HibernateLesson.seed(sessionFactory);

            assertTrue(HibernateLesson.firstLevelCacheReturnsSameInstance(sessionFactory));
            assertEquals(
                    new BigDecimal("9250.00"),
                    HibernateLesson.demonstrateDirtyChecking(sessionFactory));
            assertEquals(
                    new BigDecimal("9250.00"),
                    HibernateLesson.demonstrateRollback(sessionFactory));
        }
    }

    @Test
    void join_fetch_reduces_the_queries_seen_in_the_n_plus_one_example() {
        HibernateSqlCounter counter = new HibernateSqlCounter();
        try (SessionFactory sessionFactory = HibernateLesson.buildSessionFactory(
                "phase6-fetch-test", counter)) {
            HibernateLesson.seed(sessionFactory);

            HibernateLesson.FetchObservation nPlusOne =
                    HibernateLesson.demonstrateNPlusOne(sessionFactory, counter);
            HibernateLesson.FetchObservation joinFetch =
                    HibernateLesson.demonstrateJoinFetch(sessionFactory, counter);

            assertTrue(nPlusOne.statementCount() > joinFetch.statementCount());
            assertEquals(1, joinFetch.statementCount());
            assertEquals(
                    List.of("Engineering", "Engineering", "Operations"),
                    joinFetch.departmentNames());
        }
    }

    @Test
    void lazy_loading_requires_a_session_and_pagination_is_explicit() {
        HibernateSqlCounter counter = new HibernateSqlCounter();
        try (SessionFactory sessionFactory = HibernateLesson.buildSessionFactory(
                "phase6-query-test", counter)) {
            HibernateLesson.seed(sessionFactory);

            assertTrue(HibernateLesson.lazyCollectionRequiresSession(sessionFactory));
            assertEquals(
                    List.of("Bob Karim", "Carol Islam"),
                    HibernateLesson.paginateEmployees(sessionFactory, 1, 2));
        }
    }
}
