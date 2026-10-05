package com.jarirahmed.projects.storefront.repository;

import com.jarirahmed.projects.storefront.entity.Product;

import java.util.List;

public record ProductPage(List<Product> products, long totalElements, int page, int size) {
    public int totalPages() {
        return totalElements == 0 ? 0 : (int) ((totalElements + size - 1) / size);
    }
}
