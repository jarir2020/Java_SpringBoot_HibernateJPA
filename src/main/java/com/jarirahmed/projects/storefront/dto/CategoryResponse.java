package com.jarirahmed.projects.storefront.dto;

import com.jarirahmed.projects.storefront.entity.Category;

public record CategoryResponse(long id, String name) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
