package com.jarirahmed.projects.inventory;

import com.jarirahmed.projects.inventory.container.InventoryApplication;
import com.jarirahmed.projects.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class InventorySpringContextTest {
    @Test
    void context_scans_repository_injects_service_and_resolves_configuration() {
        try (AnnotationConfigApplicationContext context = InventoryApplication.createContext(
                Map.of(
                        "inventory.store-name", "Configured Store",
                        "inventory.low-stock-threshold", "7"))) {
            InventoryService service = context.getBean(InventoryService.class);

            assertSame(service, context.getBean(InventoryService.class));
            assertEquals("Configured Store", service.storeName());
            assertEquals(7, service.lowStockThreshold());
            assertEquals(1, context.getBeansOfType(InventoryService.class).size());
        }
    }
}
