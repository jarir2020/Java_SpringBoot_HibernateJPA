package com.jarirahmed.springapi.service;

import com.jarirahmed.hibernate.HibernateDepartment;
import com.jarirahmed.hibernate.HibernateEmployee;
import com.jarirahmed.springapi.dto.CreateEmployeeRequest;
import com.jarirahmed.springapi.dto.EmployeeResponse;
import com.jarirahmed.springapi.dto.PageResponse;
import com.jarirahmed.springapi.error.DepartmentNotFoundException;
import com.jarirahmed.springapi.error.DuplicateEmployeeEmailException;
import com.jarirahmed.springapi.error.EmployeeNotFoundException;
import com.jarirahmed.springapi.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Application layer: validates domain relationships, maps DTOs, and owns the
 * transaction boundary. Controllers remain focused on HTTP concerns.
 */
@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> findPage(String departmentName, int page, int size) {
        validatePage(page, size);
        long totalElements = repository.count(departmentName);
        int totalPages = totalElements == 0 ? 0 : (int) ((totalElements + size - 1) / size);
        var items = repository.findPage(departmentName, page, size).stream()
                .map(EmployeeResponse::from)
                .toList();
        return new PageResponse<>(items, page, size, totalElements, totalPages);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(long id) {
        HibernateEmployee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return EmployeeResponse.from(employee);
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (repository.existsByEmail(email)) {
            throw new DuplicateEmployeeEmailException(email);
        }

        String departmentName = request.departmentName().trim();
        HibernateDepartment department = repository.findDepartmentByName(departmentName)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentName));

        HibernateEmployee employee = new HibernateEmployee(
                request.fullName().trim(),
                email,
                request.salary(),
                request.hiredOn());
        department.addEmployee(employee);
        repository.save(employee);
        repository.flush();
        return EmployeeResponse.from(employee);
    }

    private static void validatePage(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be zero or greater.");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100.");
        }
    }
}
