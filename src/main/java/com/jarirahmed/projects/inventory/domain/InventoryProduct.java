package com.jarirahmed.projects.inventory.domain;

import com.jarirahmed.projects.inventory.error.InsufficientStockException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A small domain object managed by the Project 2 inventory service.
 *
 * <p>The object owns stock changes so callers cannot silently create negative
 * stock. Spring does not make this class special; it is an ordinary Java
 * object created by the service.</p>
 */
public final class InventoryProduct {
    private final String sku;
    private final String name;
    private final BigDecimal price;
    private int stockQuantity;

    public InventoryProduct(String sku, String name, BigDecimal price, int stockQuantity) {
        this.sku = requireText(sku, "SKU").toUpperCase();
        this.name = requireText(name, "Product name");
        this.price = Objects.requireNonNull(price, "Price is required.");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        this.stockQuantity = stockQuantity;
    }

    public String sku() {
        return sku;
    }

    public String name() {
        return name;
    }

    public BigDecimal price() {
        return price;
    }

    public int stockQuantity() {
        return stockQuantity;
    }

    public void restock(int quantity) {
        requirePositive(quantity);
        stockQuantity += quantity;
    }

    public void sell(int quantity) {
        requirePositive(quantity);
        if (quantity > stockQuantity) {
            throw new InsufficientStockException(sku, stockQuantity, quantity);
        }
        stockQuantity -= quantity;
    }

    public String summary() {
        return sku + " | " + name + " | " + price + " | stock=" + stockQuantity;
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
    }
}
