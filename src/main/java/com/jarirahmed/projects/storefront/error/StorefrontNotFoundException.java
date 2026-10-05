package com.jarirahmed.projects.storefront.error;

/** Returned when a requested storefront entity is missing. */
public final class StorefrontNotFoundException extends RuntimeException {
    public StorefrontNotFoundException(String resource, long id) {
        super("No " + resource + " exists with id " + id + ".");
    }

    public StorefrontNotFoundException(String resource, String key) {
        super("No " + resource + " exists for " + key + ".");
    }
}
