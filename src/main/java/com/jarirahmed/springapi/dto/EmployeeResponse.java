package com.jarirahmed.springapi.dto;

import com.jarirahmed.hibernate.HibernateEmployee;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO: the database entity stays behind the API boundary.
 * In particular, assignments and persistence implementation details are not
 * accidentally serialized into the public response.
 */
public record EmployeeResponse(
        Long id,
        String fullName,
        String email,
        BigDecimal salary,
        LocalDate hiredOn,
        String departmentName,
        long version) {

    /** Manual mapping is intentionally visible before MapStruct is introduced. */
    public static EmployeeResponse from(HibernateEmployee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getSalary(),
                employee.getHiredOn(),
                employee.getDepartment().getName(),
                employee.getVersion());
    }
}
