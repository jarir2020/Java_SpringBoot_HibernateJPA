package com.jarirahmed.projects.blog.dto;

import com.jarirahmed.projects.blog.domain.BlogUser;

public record UserResponse(long id, String displayName, String email) {
    public static UserResponse from(BlogUser user) {
        return new UserResponse(user.id(), user.displayName(), user.email());
    }
}
