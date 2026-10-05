package com.jarirahmed.springapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO: clients can submit only fields that this endpoint accepts.
 * Validation belongs at this boundary, before domain work starts.
 */
public record CreateEmployeeRequest(
        @NotBlank(message = "Full name is required.")
        @Size(max = 100, message = "Full name must be at most 100 characters.")
        String fullName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        @Size(max = 160, message = "Email must be at most 160 characters.")
        String email,

        @NotNull(message = "Salary is required.")
        @DecimalMin(value = "0.01", message = "Salary must be greater than zero.")
        @Digits(integer = 10, fraction = 2, message = "Salary must have at most two decimal places.")
        BigDecimal salary,

        @NotNull(message = "Hire date is required.")
        @PastOrPresent(message = "Hire date cannot be in the future.")
        LocalDate hiredOn,

        @NotBlank(message = "Department name is required.")
        @Size(max = 80, message = "Department name must be at most 80 characters.")
        String departmentName) {
}
