package com.jarirahmed.projects.storefront.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
        @NotBlank(message = "Shipping name is required.")
        String shippingName,
        @NotBlank(message = "Shipping email is required.")
        @Email(message = "Shipping email must be valid.")
        String shippingEmail) {
}
