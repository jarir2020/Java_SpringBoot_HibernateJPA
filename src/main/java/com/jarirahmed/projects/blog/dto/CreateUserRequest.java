package com.jarirahmed.projects.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Display name is required.")
        @Size(max = 80, message = "Display name must be 80 characters or fewer.")
        String displayName,
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        String email) {
}
