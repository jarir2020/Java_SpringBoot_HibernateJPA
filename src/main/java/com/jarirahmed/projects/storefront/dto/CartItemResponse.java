package com.jarirahmed.projects.storefront.dto;

import com.jarirahmed.projects.storefront.entity.CartItem;

import java.math.BigDecimal;

public record CartItemResponse(
        long productId,
        String name,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal) {
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.lineTotal());
    }
}
