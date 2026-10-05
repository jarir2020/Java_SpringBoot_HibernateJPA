package com.jarirahmed.springmvc.model;

import java.util.List;

/** Includes request metadata so header, cookie, query, and JSON response concepts are visible. */
public record PostListResponse(
        List<PostResponse> posts,
        String clientName,
        String courseMode,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
