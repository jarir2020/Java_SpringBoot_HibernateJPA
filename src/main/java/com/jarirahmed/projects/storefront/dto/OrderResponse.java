package com.jarirahmed.projects.storefront.dto;

import com.jarirahmed.projects.storefront.entity.StoreOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        long id,
        String status,
        BigDecimal total,
        Instant createdAt,
        String paymentStatus,
        List<OrderItemResponse> items) {
    public static OrderResponse from(StoreOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getPayment().getStatus().name(),
                order.getItems().stream().map(OrderItemResponse::from).toList());
    }
}
