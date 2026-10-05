package com.jarirahmed.projects.inventory;

import com.jarirahmed.projects.inventory.config.InventoryProperties;
import com.jarirahmed.projects.inventory.domain.InventoryProduct;
import com.jarirahmed.projects.inventory.error.DuplicateSkuException;
import com.jarirahmed.projects.inventory.error.InsufficientStockException;
import com.jarirahmed.projects.inventory.repository.InMemoryInventoryRepository;
import com.jarirahmed.projects.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryServiceTest {
    private final InventoryService service = new InventoryService(
            new InMemoryInventoryRepository(),
            new InventoryProperties("Test Store", 2));

    @Test
    void service_adds_sells_and_finds_products_without_a_container() {
        InventoryProduct product = service.addProduct(
                " bk-1 ", "Book", new BigDecimal("10.00"), 4);

        assertEquals("BK-1", product.sku());
        assertEquals(4, service.findProduct("BK-1").stockQuantity());

        service.sell("bk-1", 2);

        assertEquals(2, service.findProduct("BK-1").stockQuantity());
        assertEquals(List.of("BK-1"), service.lowStockProducts().stream()
                .map(InventoryProduct::sku)
                .toList());
    }

    @Test
    void service_rejects_duplicate_skus_and_overselling() {
        service.addProduct("BK-1", "Book", new BigDecimal("10.00"), 1);

        assertThrows(DuplicateSkuException.class,
                () -> service.addProduct("bk-1", "Another book", new BigDecimal("11.00"), 1));
        assertThrows(InsufficientStockException.class,
                () -> service.sell("BK-1", 2));
    }
}
