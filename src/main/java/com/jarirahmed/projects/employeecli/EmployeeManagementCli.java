package com.jarirahmed.projects.employeecli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Console adapter for Project 1. The reader and writer are injected so the
 * interactive behavior can be tested with StringReader/StringWriter.
 */
public final class EmployeeManagementCli {
    private final EmployeeManagementService service;
    private final BufferedReader input;
    private final PrintWriter output;
    private final Path defaultFile;

    public EmployeeManagementCli(
            EmployeeManagementService service,
            BufferedReader input,
            PrintWriter output,
            Path defaultFile) {
        this.service = service;
        this.input = input;
        this.output = output;
        this.defaultFile = defaultFile;
    }

    public void run() throws IOException {
        output.println("=== PROJECT 1: EMPLOYEE MANAGEMENT SYSTEM ===");
        output.println("Type 'help' for commands. Type 'exit' to quit.");
        output.flush();

        String line;
        while ((line = input.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            try {
                if (execute(line.trim())) {
                    return;
                }
            } catch (IllegalArgumentException
                     | EmployeeNotFoundException
                     | DuplicateEmployeeException
                     | EmployeeStorageException exception) {
                output.println("Error: " + exception.getMessage());
            } catch (IOException exception) {
                output.println("File error: " + exception.getMessage());
            }
            output.flush();
        }
    }

    public static void launch(InputStream input, OutputStream output, Path defaultFile)
            throws IOException {
        EmployeeManagementService service = new EmployeeManagementService(
                new InMemoryEmployeeRepository(),
                new EmployeeFileRepository());
        EmployeeManagementCli cli = new EmployeeManagementCli(
                service,
                new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)),
                new PrintWriter(output, true, StandardCharsets.UTF_8),
                defaultFile);
        cli.run();
    }

    private boolean execute(String line) throws IOException {
        String[] commandParts = line.split("\\s+", 2);
        String command = commandParts[0].toLowerCase();
        String argument = commandParts.length == 2 ? commandParts[1].trim() : "";

        return switch (command) {
            case "help" -> {
                printHelp();
                yield false;
            }
            case "add" -> {
                add(argument);
                yield false;
            }
            case "list" -> {
                list();
                yield false;
            }
            case "find" -> {
                output.println(service.findById(parseId(argument)).summary());
                yield false;
            }
            case "search" -> {
                service.search(argument).forEach(employee -> output.println(employee.summary()));
                yield false;
            }
            case "deactivate" -> {
                service.deactivate(parseId(argument));
                output.println("Employee deactivated.");
                yield false;
            }
            case "activate" -> {
                service.activate(parseId(argument));
                output.println("Employee activated.");
                yield false;
            }
            case "payroll" -> {
                service.payrollByDepartment().forEach(
                        (department, total) -> output.println(department + " -> " + total));
                yield false;
            }
            case "save" -> {
                Path path = argument.isBlank() ? defaultFile : Path.of(argument);
                service.save(path);
                output.println("Saved " + service.size() + " employees to " + path + ".");
                yield false;
            }
            case "load" -> {
                Path path = argument.isBlank() ? defaultFile : Path.of(argument);
                service.load(path);
                output.println("Loaded " + service.size() + " employees from " + path + ".");
                yield false;
            }
            case "exit", "quit" -> {
                output.println("Goodbye.");
                yield true;
            }
            default -> {
                output.println("Unknown command. Type 'help'.");
                yield false;
            }
        };
    }

    private void add(String argument) {
        String[] fields = argument.split("\\|", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException(
                    "Use: add id|name|department|monthlySalary");
        }
        EmployeeRecord employee = service.add(
                parseId(fields[0]),
                fields[1],
                fields[2],
                new BigDecimal(fields[3].trim()));
        output.println("Added: " + employee.summary());
    }

    private void list() {
        if (service.listAll().isEmpty()) {
            output.println("No employees found.");
            return;
        }
        service.listAll().forEach(employee -> output.println(employee.summary()));
    }

    private void printHelp() {
        output.println("add id|name|department|monthlySalary");
        output.println("list");
        output.println("find id");
        output.println("search text");
        output.println("activate id");
        output.println("deactivate id");
        output.println("payroll");
        output.println("save [path]");
        output.println("load [path]");
        output.println("exit");
    }

    private static int parseId(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("Employee id is required.");
        }
        return Integer.parseInt(value.trim());
    }
}
