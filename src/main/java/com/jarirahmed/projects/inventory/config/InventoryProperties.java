package com.jarirahmed.projects.inventory.config;

/** Configuration values supplied to the inventory service by Spring. */
public record InventoryProperties(String storeName, int lowStockThreshold) {
    public InventoryProperties {
        if (storeName == null || storeName.isBlank()) {
            throw new IllegalArgumentException("Store name is required.");
        }
        if (lowStockThreshold < 0) {
            throw new IllegalArgumentException("Low-stock threshold cannot be negative.");
        }
        storeName = storeName.trim();
    }
}
