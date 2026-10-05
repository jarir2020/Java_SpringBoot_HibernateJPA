package com.jarirahmed.springmvc.model;

import com.jarirahmed.springmvc.validation.ValidPublication;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request DTO: clients may submit only fields accepted by this API boundary. */
@ValidPublication
public record CreatePostRequest(
        @NotBlank(message = "Title is required.")
        @Size(max = 100, message = "Title must be at most 100 characters.")
        String title,

        @NotBlank(message = "Body is required.")
        @Size(min = 10, max = 2000, message = "Body must contain 10 to 2000 characters.")
        String body,

        @NotBlank(message = "Author email is required.")
        @Email(message = "Author email must be valid.")
        String authorEmail,

        @NotBlank(message = "Category is required.")
        @Size(max = 40, message = "Category must be at most 40 characters.")
        String category,

        boolean published) {
}
