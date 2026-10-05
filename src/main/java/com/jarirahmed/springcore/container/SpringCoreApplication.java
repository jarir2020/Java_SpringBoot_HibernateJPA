package com.jarirahmed.springcore.container;

import com.jarirahmed.springcore.config.SpringCoreConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/** Creates and configures the ApplicationContext used by the lesson and tests. */
public final class SpringCoreApplication {
    private SpringCoreApplication() {
    }

    public static AnnotationConfigApplicationContext createContext(
            Map<String, String> properties) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // Add properties before refresh so @Value placeholders are resolved while beans start.
        Map<String, Object> values = new HashMap<>(properties);
        context.getEnvironment().getPropertySources().addFirst(
                new MapPropertySource("lessonProperties", values));
        context.register(SpringCoreConfiguration.class);
        context.refresh();
        return context;
    }
}
