package com.jarirahmed.projects.employeecli;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** The console adapter can be tested with in-memory input and output streams. */
class EmployeeManagementCliTest {
    @Test
    void executes_commands_and_reports_errors_without_system_io() throws Exception {
        String commands = String.join("\n",
                "add 1|Alice|Engineering|5000",
                "add 2|Bob|Operations|3000",
                "deactivate 2",
                "search engineering",
                "payroll",
                "find 99",
                "exit");
        StringWriter captured = new StringWriter();
        EmployeeManagementService service = new EmployeeManagementService(
                new InMemoryEmployeeRepository(),
                new EmployeeFileRepository());
        EmployeeManagementCli cli = new EmployeeManagementCli(
                service,
                new BufferedReader(new StringReader(commands)),
                new PrintWriter(captured, true),
                Files.createTempFile("employees-cli-", ".txt"));

        cli.run();

        String output = captured.toString();
        assertTrue(output.contains("Added: 1 | Alice | Engineering | 5000 | ACTIVE"));
        assertTrue(output.contains("Employee deactivated."));
        assertTrue(output.contains("Engineering -> 5000"));
        assertTrue(output.contains("Error: No employee exists with id 99."));
        assertTrue(output.contains("Goodbye."));
    }
}
