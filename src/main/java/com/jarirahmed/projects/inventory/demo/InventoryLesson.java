package com.jarirahmed.projects.inventory.demo;

import com.jarirahmed.projects.inventory.container.InventoryApplication;
import com.jarirahmed.projects.inventory.domain.InventoryProduct;
import com.jarirahmed.projects.inventory.service.InventoryService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.Map;

/** Runs the Project 2 inventory example from a plain Spring context. */
public final class InventoryLesson {
    private InventoryLesson() {
    }

    public static void run() {
        System.out.println("\n=== PROJECT 2: SPRING CORE INVENTORY ===");

        try (AnnotationConfigApplicationContext context = InventoryApplication.createContext(
                Map.of(
                        "inventory.store-name", "Learning Supply Store",
                        "inventory.low-stock-threshold", "3"))) {
            InventoryService service = context.getBean(InventoryService.class);
            service.addProduct("BK-101", "Spring Core Notes", new BigDecimal("24.99"), 12);
            service.addProduct("MG-202", "Java Mug", new BigDecimal("8.50"), 3);
            service.addProduct("KB-303", "Learning Keyboard", new BigDecimal("39.00"), 1);
            service.sell("MG-202", 1);

            System.out.println("Store: " + service.storeName());
            System.out.println("All products:");
            service.listProducts().forEach(product -> System.out.println("  " + product.summary()));
            System.out.println("Low-stock products (<= " + service.lowStockThreshold() + "):");
            service.lowStockProducts().forEach(product -> System.out.println("  " + product.summary()));
            System.out.println("Container owns service: " + (service == context.getBean(InventoryService.class)));
        }

        System.out.println("PROJECT 2 COMPLETE");
    }
}
