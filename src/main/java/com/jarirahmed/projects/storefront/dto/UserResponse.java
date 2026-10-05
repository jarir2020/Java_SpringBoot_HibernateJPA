package com.jarirahmed.projects.storefront.dto;

import com.jarirahmed.projects.storefront.entity.StoreUser;

public record UserResponse(long id, String login, String displayName, String email) {
    public static UserResponse from(StoreUser user) {
        return new UserResponse(user.getId(), user.getLogin(), user.getDisplayName(), user.getEmail());
    }
}
