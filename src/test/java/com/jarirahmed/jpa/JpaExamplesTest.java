package com.jarirahmed.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies the standard JPA API with Hibernate acting as the provider. */
class JpaExamplesTest {
    @Test
    void persistence_unit_supports_jpql_and_criteria_queries() {
        try (EntityManagerFactory factory = JpaLesson.buildEntityManagerFactory(
                "phase7-query-test")) {
            JpaLesson.seed(factory);

            assertEquals(
                    List.of("Alice Rahman", "Bob Karim"),
                    JpaLesson.jpqlHighEarners(factory, new BigDecimal("6000.00")));
            assertEquals(
                    List.of("Alice Rahman", "Bob Karim"),
                    JpaLesson.criteriaEmployeesInDepartment(factory, "Engineering"));
        }
    }

    @Test
    void entity_manager_exposes_persistence_context_and_lifecycle_operations() {
        try (EntityManagerFactory factory = JpaLesson.buildEntityManagerFactory(
                "phase7-lifecycle-test")) {
            JpaLesson.seed(factory);

            assertTrue(JpaLesson.firstLevelCacheReturnsSameInstance(factory));
            JpaLesson.LifecycleObservation lifecycle = JpaLesson.demonstrateLifecycle(factory);
            assertTrue(lifecycle.managedAfterPersist());
            assertTrue(lifecycle.detachedAfterClear());
            assertTrue(lifecycle.mergeReturnedManagedCopy());
        }
    }

    @Test
    void version_field_rejects_a_stale_optimistic_update() {
        try (EntityManagerFactory factory = JpaLesson.buildEntityManagerFactory(
                "phase7-locking-test")) {
            JpaLesson.seed(factory);

            JpaLesson.OptimisticLockObservation locking =
                    JpaLesson.demonstrateOptimisticLocking(factory);
            assertTrue(locking.staleUpdateRejected());
            assertTrue(locking.versionAfter() > locking.versionBefore());
            assertEquals(new BigDecimal("9100.00"), locking.finalSalary());
        }
    }

    @Test
    void entity_graph_and_pagination_are_standard_jpa_fetch_tools() {
        try (EntityManagerFactory factory = JpaLesson.buildEntityManagerFactory(
                "phase7-fetch-test")) {
            JpaLesson.seed(factory);

            JpaLesson.EntityGraphObservation graph = JpaLesson.demonstrateEntityGraph(factory);
            assertTrue(graph.departmentLoaded());
            assertEquals(
                    List.of("Engineering", "Engineering", "Operations"),
                    graph.departmentNames());
            assertEquals(
                    List.of("Bob Karim", "Carol Islam"),
                    JpaLesson.paginateEmployees(factory, 1, 2));
        }
    }
}
