package com.jarirahmed.springmvc.container;

import com.jarirahmed.springmvc.config.SpringMvcConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/** Creates the WebApplicationContext used by the Phase 3 lesson and tests. */
public final class SpringMvcApplication {
    private SpringMvcApplication() {
    }

    public static AnnotationConfigWebApplicationContext createContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        // A real deployment supplies this from the servlet container. MockMvc supplies
        // the request/response objects, so the lesson provides the matching context here.
        context.setServletContext(new MockServletContext());
        context.register(SpringMvcConfiguration.class);
        context.refresh();
        return context;
    }
}
