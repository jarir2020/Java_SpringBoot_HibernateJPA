package com.jarirahmed.projects.employeecli;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Reads and writes employee records using the Java NIO file APIs. */
public final class EmployeeFileRepository {
    private static final String HEADER = "id|name|department|monthlySalary|status";

    public void save(Path path, Collection<EmployeeRecord> employees) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        Path parent = absolutePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        employees.stream()
                .map(EmployeeRecord::toFileLine)
                .forEach(lines::add);
        Files.write(
                absolutePath,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    public List<EmployeeRecord> load(Path path) throws IOException {
        if (!Files.exists(path)) {
            return List.of();
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return List.of();
        }
        if (!HEADER.equals(lines.getFirst())) {
            throw new EmployeeStorageException("Invalid employee file header.");
        }

        Set<Integer> ids = new HashSet<>();
        List<EmployeeRecord> employees = new ArrayList<>();
        for (int index = 1; index < lines.size(); index++) {
            if (lines.get(index).isBlank()) {
                continue;
            }
            EmployeeRecord employee = EmployeeRecord.fromFileLine(lines.get(index), index + 1);
            if (!ids.add(employee.id())) {
                throw new EmployeeStorageException(
                        "Invalid employee file at line " + (index + 1)
                                + ": duplicate employee id " + employee.id() + ".");
            }
            employees.add(employee);
        }
        return employees;
    }
}
