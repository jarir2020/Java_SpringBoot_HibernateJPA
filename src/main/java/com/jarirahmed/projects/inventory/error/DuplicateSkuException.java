package com.jarirahmed.projects.inventory.error;

/** Raised when an inventory item uses an existing SKU. */
public final class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String sku) {
        super("An inventory product already exists for SKU: " + sku);
    }
}
