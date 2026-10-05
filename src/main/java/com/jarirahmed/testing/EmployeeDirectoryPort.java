package com.jarirahmed.testing;

import java.util.Optional;

/** Small application boundary that can be replaced by a Mockito mock in a unit test. */
public interface EmployeeDirectoryPort {
    Optional<EmployeeSummary> findById(long id);
}
