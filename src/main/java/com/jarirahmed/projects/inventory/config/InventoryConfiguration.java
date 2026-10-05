package com.jarirahmed.projects.inventory.config;

import com.jarirahmed.projects.inventory.repository.InMemoryInventoryRepository;
import com.jarirahmed.projects.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Explicit composition root for Project 2.
 *
 * <p>Spring Core is easier to understand when the application context is
 * created directly. This configuration scans the repository and service
 * stereotypes, then creates the configuration object with a named bean.</p>
 */
@Configuration
@ComponentScan(basePackageClasses = {
        InventoryService.class,
        InMemoryInventoryRepository.class
})
public class InventoryConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public InventoryProperties inventoryProperties(
            @Value("${inventory.store-name:Learning Store}") String storeName,
            @Value("${inventory.low-stock-threshold:5}") int lowStockThreshold) {
        return new InventoryProperties(storeName, lowStockThreshold);
    }
}
