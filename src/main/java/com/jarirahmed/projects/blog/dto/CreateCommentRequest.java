package com.jarirahmed.projects.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @Positive(message = "Comment author is required.")
        long userId,
        @NotBlank(message = "Comment body is required.")
        @Size(min = 2, max = 1000, message = "Comment must contain 2 to 1000 characters.")
        String body) {
}
