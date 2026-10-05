package com.jarirahmed.projects.inventory.error;

/** Raised when a service operation references an unknown SKU. */
public final class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String sku) {
        super("No inventory product exists for SKU: " + sku);
    }
}
