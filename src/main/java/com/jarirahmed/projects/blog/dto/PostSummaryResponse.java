package com.jarirahmed.projects.blog.dto;

public record PostSummaryResponse(
        long id,
        String title,
        String excerpt,
        UserResponse author,
        boolean published,
        int commentCount) {
}
