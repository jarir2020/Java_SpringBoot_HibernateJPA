package com.jarirahmed.springapi.dto;

import java.util.List;

/** Metadata makes pagination part of the API contract instead of a hidden query detail. */
public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
