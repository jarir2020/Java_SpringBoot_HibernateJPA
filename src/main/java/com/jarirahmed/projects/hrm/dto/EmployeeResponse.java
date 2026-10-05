package com.jarirahmed.projects.hrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String employeeNumber,
        String fullName,
        String email,
        String jobTitle,
        BigDecimal monthlySalary,
        LocalDate hireDate,
        String companyCode,
        boolean active) {
}
