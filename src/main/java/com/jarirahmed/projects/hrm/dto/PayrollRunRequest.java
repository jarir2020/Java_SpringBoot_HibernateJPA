package com.jarirahmed.projects.hrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PayrollRunRequest(
        @NotBlank String companyCode,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "Period must use YYYY-MM format") String period) {
}
