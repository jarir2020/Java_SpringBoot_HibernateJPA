package com.jarirahmed.projects.blog.dto;

import com.jarirahmed.projects.blog.domain.BlogComment;

public record CommentResponse(
        long id,
        long postId,
        UserResponse author,
        String body) {
    public static CommentResponse from(BlogComment comment, UserResponse author) {
        return new CommentResponse(comment.id(), comment.postId(), author, comment.body());
    }
}
