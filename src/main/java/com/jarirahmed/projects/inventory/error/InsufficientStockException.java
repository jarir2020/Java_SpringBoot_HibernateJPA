package com.jarirahmed.projects.inventory.error;

/** Raised when a sale would make a product's stock negative. */
public final class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String sku, int available, int requested) {
        super("SKU " + sku + " has " + available + " items; requested " + requested + ".");
    }
}
