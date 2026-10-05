package com.jarirahmed.projects.storefront.dto;

import jakarta.validation.constraints.Positive;

public record AddToCartRequest(
        @Positive(message = "Product id must be positive.")
        long productId,
        @Positive(message = "Quantity must be positive.")
        int quantity) {
}
