package com.jarirahmed.projects.inventory.repository;

import com.jarirahmed.projects.inventory.domain.InventoryProduct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A deliberately small repository implementation.
 *
 * <p>Project 2 uses a map so the Spring boundaries stay visible. A later
 * project can replace this class with a database repository without changing
 * the service contract.</p>
 */
@Repository
public class InMemoryInventoryRepository implements InventoryRepository {
    private final Map<String, InventoryProduct> products = new LinkedHashMap<>();

    @Override
    public Optional<InventoryProduct> findBySku(String sku) {
        return Optional.ofNullable(products.get(sku));
    }

    @Override
    public List<InventoryProduct> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public InventoryProduct save(InventoryProduct product) {
        products.put(product.sku(), product);
        return product;
    }
}
