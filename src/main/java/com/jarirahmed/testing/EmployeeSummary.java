package com.jarirahmed.testing;

/** Immutable data crossing the unit-test example's repository boundary. */
public record EmployeeSummary(long id, String fullName, String department) {
}
