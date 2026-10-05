package com.jarirahmed.projects.storefront.error;

/** Checkout cannot continue when requested quantity exceeds inventory. */
public final class OutOfStockException extends RuntimeException {
    public OutOfStockException(Long productId, String productName, int available, int requested) {
        super("Product " + productName + " (" + productId + ") has "
                + available + " items; requested " + requested + ".");
    }
}
