package com.jarirahmed.testing;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * A runnable starting point for a real database integration test.
 *
 * It is disabled because it needs Docker and deliberately does not affect the
 * default course build. Enable it locally after reviewing image and schema
 * choices for the application under test.
 */
@Disabled("Enable when Docker is available and a PostgreSQL test schema is configured")
@Testcontainers
class Phase11TestcontainersTemplateTest {
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void container_connection_details_are_available_to_the_test() {
        System.out.println("JDBC URL: " + postgres.getJdbcUrl());
    }
}
