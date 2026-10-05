package com.jarirahmed.projects.blog.dto;

import java.util.List;

public record PostDetailResponse(
        long id,
        String title,
        String body,
        UserResponse author,
        boolean published,
        List<CommentResponse> comments) {
}
