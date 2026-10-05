package com.jarirahmed.projects.employeecli;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests project rules without starting Spring or touching the real filesystem. */
class EmployeeManagementServiceTest {
    @Test
    void manages_records_and_calculates_active_payroll_by_department() {
        EmployeeManagementService service = service();
        service.add(2, "Bob", "Operations", new BigDecimal("3000"));
        service.add(1, "Alice", "Engineering", new BigDecimal("5000"));
        service.add(3, "Carol", "Engineering", new BigDecimal("4500"));

        service.deactivate(3);

        assertEquals(List.of(1, 2, 3), service.listAll().stream()
                .map(EmployeeRecord::id)
                .toList());
        assertEquals(new BigDecimal("5000"), service.payrollByDepartment().get("Engineering"));
        assertEquals(new BigDecimal("3000"), service.payrollByDepartment().get("Operations"));
        assertEquals(List.of(1, 3), service.search("engineering").stream()
                .map(EmployeeRecord::id)
                .toList());
        assertTrue(service.findById(1).isActive());
    }

    @Test
    void rejects_duplicate_and_missing_ids() {
        EmployeeManagementService service = service();
        service.add(1, "Alice", "Engineering", new BigDecimal("5000"));

        assertThrows(
                DuplicateEmployeeException.class,
                () -> service.add(1, "Another", "Operations", new BigDecimal("3000")));
        assertThrows(EmployeeNotFoundException.class, () -> service.findById(99));
    }

    private static EmployeeManagementService service() {
        return new EmployeeManagementService(
                new InMemoryEmployeeRepository(),
                new EmployeeFileRepository());
    }
}
