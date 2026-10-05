package com.jarirahmed.projects.storefront.entity;

import com.jarirahmed.projects.storefront.error.OutOfStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "shop_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String sku;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "inventory_quantity", nullable = false)
    private int inventoryQuantity;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    protected Product() {
        // JPA needs a no-argument constructor.
    }

    public Product(
            String sku,
            String name,
            String description,
            BigDecimal price,
            int inventoryQuantity,
            boolean active) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
        this.active = active;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void decreaseInventory(int quantity) {
        if (quantity > inventoryQuantity) {
            throw new OutOfStockException(id, name, inventoryQuantity, quantity);
        }
        inventoryQuantity -= quantity;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public boolean isActive() {
        return active;
    }

    public Category getCategory() {
        return category;
    }
}
