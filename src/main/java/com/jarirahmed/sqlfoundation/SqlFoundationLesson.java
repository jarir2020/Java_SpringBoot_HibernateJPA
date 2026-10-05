package com.jarirahmed.sqlfoundation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Phase 5 uses JDBC directly so the SQL remains visible before an ORM is
 * introduced. H2 is only the repeatable local database for this lesson.
 */
public final class SqlFoundationLesson {
    private SqlFoundationLesson() {
    }

    public static void run() throws SQLException {
        System.out.println("\n=== PHASE 5: SQL + DATABASE FOUNDATION ===");
        System.out.println("Database: H2 in-memory (PostgreSQL-compatible learning mode)");
        System.out.println("Access layer: java.sql JDBC; Hibernate/JPA comes later");

        try (Connection connection = openDatabase()) {
            System.out.println("Tables: " + tableNames(connection));
            System.out.println("Join rows: " + employeeProjectRows(connection));
            System.out.println("Department payroll view: " + departmentPayroll(connection));
            System.out.println("Above-average earners: " + employeesAboveAverageSalary(connection));
            System.out.println("Indexes on employee: " + indexNames(connection, "EMPLOYEE"));

            boolean rollbackWorked = demonstrateRollback(connection);
            System.out.println("Transaction rollback preserved assignment count: " + rollbackWorked);

            String rejectedState = duplicateEmailSqlState(connection);
            System.out.println("Duplicate email rejected by UNIQUE constraint: " + rejectedState);
        }

        System.out.println("PHASE 5 COMPLETE");
    }

    /** Opens a fresh named in-memory database and loads the two SQL scripts. */
    public static Connection openDatabase() throws SQLException {
        return openDatabase("phase5");
    }

    /**
     * A name parameter lets tests use an isolated database while keeping the
     * lesson's connection URL easy to understand.
     */
    public static Connection openDatabase(String databaseName) throws SQLException {
        loadH2Driver();
        Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:" + databaseName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                "");
        initialize(connection);
        return connection;
    }

    /** Loads schema first, then data, just like a small migration bootstrap. */
    public static void initialize(Connection connection) throws SQLException {
        runScript(connection, "/db/phase5/schema.sql");
        runScript(connection, "/db/phase5/data.sql");
    }

    public static List<String> tableNames(Connection connection) throws SQLException {
        Set<String> names = new LinkedHashSet<>();
        DatabaseMetaData metadata = connection.getMetaData();
        // Restrict metadata to the user's PUBLIC schema so H2's internal
        // compatibility tables do not distract from the lesson's four tables.
        try (ResultSet tables = metadata.getTables(null, "PUBLIC", "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String name = tables.getString("TABLE_NAME");
                names.add(name.toLowerCase());
            }
        }
        return names.stream().sorted().toList();
    }

    /** Demonstrates inner joins through the reusable view created in schema.sql. */
    public static List<EmployeeProjectRow> employeeProjectRows(Connection connection)
            throws SQLException {
        String sql = """
                SELECT employee_name, department_name, project_name, assignment_role
                FROM v_employee_project
                ORDER BY employee_id, project_id
                """;
        List<EmployeeProjectRow> rows = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                rows.add(new EmployeeProjectRow(
                        result.getString("employee_name"),
                        result.getString("department_name"),
                        result.getString("project_name"),
                        result.getString("assignment_role")));
            }
        }
        return rows;
    }

    /** Demonstrates LEFT JOIN, COUNT, SUM, AVG, GROUP BY, and a view. */
    public static List<DepartmentPayrollRow> departmentPayroll(Connection connection)
            throws SQLException {
        String sql = """
                SELECT department_name, employee_count, total_salary, average_salary
                FROM v_department_payroll
                WHERE employee_count > 0
                ORDER BY total_salary DESC
                """;
        List<DepartmentPayrollRow> rows = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                rows.add(new DepartmentPayrollRow(
                        result.getString("department_name"),
                        result.getInt("employee_count"),
                        result.getBigDecimal("total_salary"),
                        result.getBigDecimal("average_salary")));
            }
        }
        return rows;
    }

    /** Demonstrates a scalar subquery: compare every salary with the average. */
    public static List<EmployeeSalaryRow> employeesAboveAverageSalary(Connection connection)
            throws SQLException {
        String sql = """
                SELECT full_name, salary
                FROM employee
                WHERE salary > (SELECT AVG(salary) FROM employee)
                ORDER BY salary DESC
                """;
        List<EmployeeSalaryRow> rows = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                rows.add(new EmployeeSalaryRow(
                        result.getString("full_name"),
                        result.getBigDecimal("salary")));
            }
        }
        return rows;
    }

    /**
     * Inserts a relationship, then rolls it back. No partially completed
     * transaction should remain visible to later work.
     */
    public static boolean demonstrateRollback(Connection connection) throws SQLException {
        int before = assignmentCount(connection);
        boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            String sql = """
                    INSERT INTO employee_project
                        (employee_id, project_id, assignment_role, hours_per_week, assigned_on)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, 1);
                statement.setInt(2, 2);
                statement.setString(3, "Reviewer");
                statement.setBigDecimal(4, new BigDecimal("5.00"));
                statement.setDate(5, Date.valueOf("2026-01-01"));
                statement.executeUpdate();
            }
            connection.rollback();
            return before == assignmentCount(connection);
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    public static int assignmentCount(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM employee_project")) {
            result.next();
            return result.getInt(1);
        }
    }

    /** Returns the SQL state produced by the duplicate-email constraint. */
    public static String duplicateEmailSqlState(Connection connection) throws SQLException {
        String sql = "INSERT INTO employee "
                + "(department_id, full_name, email, salary, hired_on) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, 1);
            statement.setString(2, "Duplicate Example");
            statement.setString(3, "alice@example.com");
            statement.setBigDecimal(4, new BigDecimal("1000.00"));
            statement.setDate(5, Date.valueOf("2026-01-01"));
            statement.executeUpdate();
            return "NOT_REJECTED";
        } catch (SQLException exception) {
            return exception.getSQLState();
        }
    }

    public static List<String> indexNames(Connection connection, String tableName)
            throws SQLException {
        Set<String> names = new LinkedHashSet<>();
        try (ResultSet indexes = connection.getMetaData().getIndexInfo(
                null, null, tableName, false, false)) {
            while (indexes.next()) {
                String name = indexes.getString("INDEX_NAME");
                if (name != null) {
                    names.add(name.toLowerCase());
                }
            }
        }
        return names.stream().sorted().toList();
    }

    private static void loadH2Driver() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("H2 runtime dependency is missing", exception);
        }
    }

    private static void runScript(Connection connection, String resourceName) throws SQLException {
        try (InputStream stream = SqlFoundationLesson.class.getResourceAsStream(resourceName)) {
            if (stream == null) {
                throw new SQLException("SQL resource was not found: " + resourceName);
            }
            String script = readResource(stream);
            for (String statementText : script.split(";")) {
                String statement = removeCommentOnlyLines(statementText).trim();
                if (!statement.isEmpty()) {
                    try (Statement sql = connection.createStatement()) {
                        sql.execute(statement);
                    }
                }
            }
        } catch (IOException exception) {
            throw new SQLException("Could not read SQL resource: " + resourceName, exception);
        }
    }

    private static String readResource(InputStream stream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }
        }
        return content.toString();
    }

    private static String removeCommentOnlyLines(String statement) {
        return statement.lines()
                .filter(line -> !line.trim().startsWith("--"))
                .reduce((left, right) -> left + '\n' + right)
                .orElse("");
    }

    public record EmployeeProjectRow(
            String employeeName,
            String departmentName,
            String projectName,
            String role) {
    }

    public record DepartmentPayrollRow(
            String departmentName,
            int employeeCount,
            BigDecimal totalSalary,
            BigDecimal averageSalary) {
    }

    public record EmployeeSalaryRow(String employeeName, BigDecimal salary) {
    }
}
