package com.jarirahmed.springapi.demo;

import com.jarirahmed.springapi.config.SpringApiConfiguration;
import com.jarirahmed.springtransactions.SpringTransactionService;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/** Runs one complete request through the Phase 9 layered API. */
public final class SpringApiLesson {
    private SpringApiLesson() {
    }

    public static void run() throws Exception {
        System.out.println("\n=== PHASE 9: DTOS AND API ARCHITECTURE ===");
        System.out.println("HTTP → controller → service → repository → JPA entity");

        try (AnnotationConfigWebApplicationContext context = openContext("phase9-lesson")) {
            context.getBean(SpringTransactionService.class).seed();
            MockMvc mockMvc = webAppContextSetup(context).build();

            var created = mockMvc.perform(post("/api/employees")
                            .contentType(APPLICATION_JSON)
                            .content(validEmployeeJson()))
                    .andReturn();
            System.out.println("POST /api/employees -> " + created.getResponse().getStatus());
            System.out.println("Created response DTO: " + created.getResponse().getContentAsString());

            var list = mockMvc.perform(get("/api/employees")
                            .param("department", "Engineering")
                            .param("page", "0")
                            .param("size", "2"))
                    .andReturn();
            System.out.println("GET /api/employees?department=Engineering -> "
                    + list.getResponse().getStatus());
            System.out.println("Paginated API response: " + list.getResponse().getContentAsString());

            var invalid = mockMvc.perform(post("/api/employees")
                            .contentType(APPLICATION_JSON)
                            .content("{\"fullName\":\"\",\"email\":\"bad\"}"))
                    .andReturn();
            System.out.println("Invalid request -> " + invalid.getResponse().getStatus()
                    + " with field errors");
        }

        System.out.println("PHASE 9 COMPLETE");
    }

    public static AnnotationConfigWebApplicationContext openContext(String databaseName) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        // MockMvc is not a servlet container, so provide the servlet context
        // that Spring MVC needs while building handler mappings.
        context.setServletContext(new MockServletContext());
        context.getEnvironment().getPropertySources().addFirst(
                new MapPropertySource("phase9-properties", Map.of(
                        "phase8.database-name", databaseName)));
        context.register(SpringApiConfiguration.class);
        context.refresh();
        return context;
    }

    private static String validEmployeeJson() {
        return "{"
                + "\"fullName\":\"David Ahmed\","
                + "\"email\":\"david@example.com\","
                + "\"salary\":7000.00,"
                + "\"hiredOn\":\"2025-01-20\","
                + "\"departmentName\":\"Engineering\""
                + "}";
    }
}
