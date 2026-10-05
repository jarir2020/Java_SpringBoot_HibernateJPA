package com.jarirahmed.projects.storefront.error;

/** Checkout requires at least one cart item. */
public final class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("The cart is empty.");
    }
}
