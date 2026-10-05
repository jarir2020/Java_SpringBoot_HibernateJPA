package com.jarirahmed.production;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Local stand-in for a repository or remote service. The load counter makes
 * cache hits visible without needing a real database or Redis server.
 */
@Repository
public class AdvancedEmployeeStore {
    private final Map<Long, AdvancedEmployee> employees = Map.of(
            1L, new AdvancedEmployee(1, "Alice Rahman", "Engineering"),
            2L, new AdvancedEmployee(2, "Bob Karim", "Operations"));
    private final AtomicInteger loadCount = new AtomicInteger();

    public Optional<AdvancedEmployee> findById(long employeeId) {
        loadCount.incrementAndGet();
        return Optional.ofNullable(employees.get(employeeId));
    }

    public int loadCount() {
        return loadCount.get();
    }
}
