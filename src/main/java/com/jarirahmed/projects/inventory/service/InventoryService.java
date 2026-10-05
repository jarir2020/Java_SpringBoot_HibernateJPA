package com.jarirahmed.projects.inventory.service;

import com.jarirahmed.projects.inventory.config.InventoryProperties;
import com.jarirahmed.projects.inventory.domain.InventoryProduct;
import com.jarirahmed.projects.inventory.error.DuplicateSkuException;
import com.jarirahmed.projects.inventory.error.ProductNotFoundException;
import com.jarirahmed.projects.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Business operations for the inventory project.
 *
 * <p>The constructor shows dependency injection plainly: the service receives
 * a repository and configuration rather than constructing either dependency.
 * That makes this class easy to test without starting Spring.</p>
 */
@Service
public class InventoryService {
    private final InventoryRepository repository;
    private final InventoryProperties properties;

    public InventoryService(InventoryRepository repository, InventoryProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public InventoryProduct addProduct(
            String sku, String name, BigDecimal price, int stockQuantity) {
        String normalizedSku = normalizeSku(sku);
        if (repository.findBySku(normalizedSku).isPresent()) {
            throw new DuplicateSkuException(normalizedSku);
        }
        return repository.save(new InventoryProduct(normalizedSku, name, price, stockQuantity));
    }

    public InventoryProduct findProduct(String sku) {
        String normalizedSku = normalizeSku(sku);
        return repository.findBySku(normalizedSku)
                .orElseThrow(() -> new ProductNotFoundException(normalizedSku));
    }

    public List<InventoryProduct> listProducts() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(InventoryProduct::sku))
                .toList();
    }

    public InventoryProduct restock(String sku, int quantity) {
        InventoryProduct product = findProduct(sku);
        product.restock(quantity);
        return product;
    }

    public InventoryProduct sell(String sku, int quantity) {
        InventoryProduct product = findProduct(sku);
        product.sell(quantity);
        return product;
    }

    public List<InventoryProduct> lowStockProducts() {
        return listProducts().stream()
                .filter(product -> product.stockQuantity() <= properties.lowStockThreshold())
                .toList();
    }

    public String storeName() {
        return properties.storeName();
    }

    public int lowStockThreshold() {
        return properties.lowStockThreshold();
    }

    private static String normalizeSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU is required.");
        }
        return sku.trim().toUpperCase();
    }
}
