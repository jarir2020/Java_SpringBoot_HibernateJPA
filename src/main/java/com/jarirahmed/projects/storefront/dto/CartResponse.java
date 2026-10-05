package com.jarirahmed.projects.storefront.dto;

import com.jarirahmed.projects.storefront.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(long id, List<CartItemResponse> items, BigDecimal total) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getItems().stream().map(CartItemResponse::from).toList(),
                cart.total());
    }
}
