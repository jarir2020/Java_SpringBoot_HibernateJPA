package com.jarirahmed.springmvc.service;

import com.jarirahmed.springmvc.model.PostResponse;

import java.util.List;

/** Service-level page result kept separate from the HTTP response envelope. */
public record PostQueryResult(
        List<PostResponse> posts,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
