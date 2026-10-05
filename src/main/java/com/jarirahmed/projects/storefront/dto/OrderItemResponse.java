package com.jarirahmed.projects.storefront.dto;

import com.jarirahmed.projects.storefront.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.lineTotal());
    }
}
