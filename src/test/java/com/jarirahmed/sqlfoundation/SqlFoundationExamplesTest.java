package com.jarirahmed.sqlfoundation;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests the SQL lesson through JDBC, before any ORM is involved. */
class SqlFoundationExamplesTest {
    @Test
    void schema_contains_normalized_tables_and_indexes() throws Exception {
        try (Connection connection = SqlFoundationLesson.openDatabase("phase5-schema-test")) {
            assertTrue(SqlFoundationLesson.tableNames(connection).containsAll(
                    List.of("department", "employee", "project", "employee_project")));
            assertTrue(SqlFoundationLesson.indexNames(connection, "EMPLOYEE")
                    .stream()
                    .anyMatch(name -> name.contains("idx_employee_department")));
        }
    }

    @Test
    void joins_and_view_return_related_employee_project_rows() throws Exception {
        try (Connection connection = SqlFoundationLesson.openDatabase("phase5-join-test")) {
            List<SqlFoundationLesson.EmployeeProjectRow> rows =
                    SqlFoundationLesson.employeeProjectRows(connection);

            assertEquals(4, rows.size());
            assertEquals("Alice Rahman", rows.getFirst().employeeName());
            assertEquals("Engineering", rows.getFirst().departmentName());
            assertEquals("Learning Platform", rows.getFirst().projectName());
        }
    }

    @Test
    void aggregates_and_subquery_produce_expected_business_results() throws Exception {
        try (Connection connection = SqlFoundationLesson.openDatabase("phase5-query-test")) {
            List<SqlFoundationLesson.DepartmentPayrollRow> payroll =
                    SqlFoundationLesson.departmentPayroll(connection);
            List<SqlFoundationLesson.EmployeeSalaryRow> aboveAverage =
                    SqlFoundationLesson.employeesAboveAverageSalary(connection);

            assertEquals("Engineering", payroll.getFirst().departmentName());
            assertEquals(15500.00, payroll.getFirst().totalSalary().doubleValue(), 0.001);
            assertEquals(List.of("Alice Rahman", "Bob Karim"), aboveAverage.stream()
                    .map(SqlFoundationLesson.EmployeeSalaryRow::employeeName)
                    .toList());
        }
    }

    @Test
    void rollback_and_constraints_protect_data_integrity() throws Exception {
        try (Connection connection = SqlFoundationLesson.openDatabase("phase5-safety-test")) {
            assertEquals(4, SqlFoundationLesson.assignmentCount(connection));
            assertTrue(SqlFoundationLesson.demonstrateRollback(connection));
            assertEquals(4, SqlFoundationLesson.assignmentCount(connection));

            assertFalse(SqlFoundationLesson.duplicateEmailSqlState(connection)
                    .equals("NOT_REJECTED"));
        }
    }
}
