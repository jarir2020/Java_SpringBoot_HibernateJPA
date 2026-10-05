package com.jarirahmed.projects.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "Title is required.")
        @Size(min = 5, max = 120, message = "Title must contain 5 to 120 characters.")
        String title,
        @NotBlank(message = "Body is required.")
        @Size(min = 10, max = 5000, message = "Body must contain 10 to 5000 characters.")
        String body,
        @Positive(message = "Author is required.")
        long authorId,
        boolean published) {
}
