package com.jarirahmed.projects.employeecli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Collection-backed repository used by the CLI service. */
public final class InMemoryEmployeeRepository
        implements CrudRepository<EmployeeRecord, Integer> {
    private final Map<Integer, EmployeeRecord> employees = new LinkedHashMap<>();

    @Override
    public List<EmployeeRecord> findAll() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public Optional<EmployeeRecord> findById(Integer id) {
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public void save(EmployeeRecord value) {
        employees.put(value.id(), value);
    }

    @Override
    public void deleteById(Integer id) {
        employees.remove(id);
    }

    @Override
    public void deleteAll() {
        employees.clear();
    }
}
