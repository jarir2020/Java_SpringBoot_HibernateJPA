package com.jarirahmed.projects.inventory.repository;

import com.jarirahmed.projects.inventory.domain.InventoryProduct;

import java.util.List;
import java.util.Optional;

/** Storage boundary used by the inventory service. */
public interface InventoryRepository {
    Optional<InventoryProduct> findBySku(String sku);

    List<InventoryProduct> findAll();

    InventoryProduct save(InventoryProduct product);
}
