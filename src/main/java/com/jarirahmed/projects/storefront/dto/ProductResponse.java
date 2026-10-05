package com.jarirahmed.projects.storefront.dto;

import com.jarirahmed.projects.storefront.entity.Product;

import java.math.BigDecimal;

public record ProductResponse(
        long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        int inventoryQuantity,
        String category) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getInventoryQuantity(),
                product.getCategory().getName());
    }
}
