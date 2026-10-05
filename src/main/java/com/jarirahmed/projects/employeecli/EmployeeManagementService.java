package com.jarirahmed.projects.employeecli;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Application rules for the CLI, kept separate from input/output concerns. */
public final class EmployeeManagementService {
    private final CrudRepository<EmployeeRecord, Integer> repository;
    private final EmployeeFileRepository fileRepository;

    public EmployeeManagementService(
            CrudRepository<EmployeeRecord, Integer> repository,
            EmployeeFileRepository fileRepository) {
        this.repository = repository;
        this.fileRepository = fileRepository;
    }

    public EmployeeRecord add(
            int id,
            String name,
            String department,
            BigDecimal monthlySalary) {
        if (repository.findById(id).isPresent()) {
            throw new DuplicateEmployeeException(id);
        }
        EmployeeRecord employee = new EmployeeRecord(id, name, department, monthlySalary);
        repository.save(employee);
        return employee;
    }

    public EmployeeRecord findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public List<EmployeeRecord> listAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparingInt(EmployeeRecord::id))
                .toList();
    }

    public List<EmployeeRecord> search(String text) {
        String query = text == null ? "" : text.trim().toLowerCase();
        return listAll().stream()
                .filter(employee -> query.isBlank()
                        || employee.name().toLowerCase().contains(query)
                        || employee.department().toLowerCase().contains(query))
                .toList();
    }

    public Map<String, BigDecimal> payrollByDepartment() {
        return repository.findAll().stream()
                .filter(EmployeeRecord::isActive)
                .collect(Collectors.groupingBy(
                        EmployeeRecord::department,
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                EmployeeRecord::monthlySalary,
                                BigDecimal::add)));
    }

    public void deactivate(int id) {
        findById(id).deactivate();
    }

    public void activate(int id) {
        findById(id).activate();
    }

    public void save(Path path) throws IOException {
        fileRepository.save(path, repository.findAll());
    }

    public void load(Path path) throws IOException {
        List<EmployeeRecord> loaded = fileRepository.load(path);
        repository.deleteAll();
        loaded.forEach(repository::save);
    }

    public int size() {
        return repository.findAll().size();
    }
}
