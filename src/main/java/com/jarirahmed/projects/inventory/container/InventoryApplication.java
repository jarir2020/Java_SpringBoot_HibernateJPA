package com.jarirahmed.projects.inventory.container;

import com.jarirahmed.projects.inventory.config.InventoryConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/** Creates the plain Spring ApplicationContext used by Project 2. */
public final class InventoryApplication {
    private InventoryApplication() {
    }

    public static AnnotationConfigApplicationContext createContext(
            Map<String, String> properties) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // Properties must be available before refresh so @Value can resolve them.
        Map<String, Object> values = new HashMap<>(properties);
        context.getEnvironment().getPropertySources().addFirst(
                new MapPropertySource("inventoryProjectProperties", values));
        context.register(InventoryConfiguration.class);
        context.refresh();
        return context;
    }
}
