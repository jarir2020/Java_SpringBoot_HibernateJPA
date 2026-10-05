package com.jarirahmed.springsecurity.demo;

import com.jarirahmed.springsecurity.config.SpringSecurityConfiguration;
import com.jarirahmed.springtransactions.SpringTransactionService;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/** Runs authentication and authorization examples through the real filter chain. */
public final class SpringSecurityLesson {
    private SpringSecurityLesson() {
    }

    public static void run() throws Exception {
        System.out.println("\n=== PHASE 10: SPRING SECURITY ===");
        System.out.println("HTTP → security filters → controller → service");

        try (AnnotationConfigWebApplicationContext context = openContext("phase10-lesson")) {
            context.getBean(SpringTransactionService.class).seed();
            MockMvc mockMvc = webAppContextSetup(context)
                    .addFilters(context.getBean(FilterChainProxy.class))
                    .build();

            int publicStatus = mockMvc.perform(get("/api/security/public"))
                    .andReturn().getResponse().getStatus();
            int anonymousStatus = mockMvc.perform(get("/api/employees"))
                    .andReturn().getResponse().getStatus();
            int readerStatus = mockMvc.perform(get("/api/employees")
                            .header("Authorization", basic("reader", "reader-password")))
                    .andReturn().getResponse().getStatus();
            int readerCreateStatus = mockMvc.perform(post("/api/employees")
                            .header("Authorization", basic("reader", "reader-password"))
                            .contentType(APPLICATION_JSON)
                            .content(validEmployeeJson("reader-created@example.com")))
                    .andReturn().getResponse().getStatus();
            int adminCreateStatus = mockMvc.perform(post("/api/employees")
                            .header("Authorization", basic("admin", "admin-password"))
                            .contentType(APPLICATION_JSON)
                            .content(validEmployeeJson("admin-created@example.com")))
                    .andReturn().getResponse().getStatus();
            int methodSecurityStatus = mockMvc.perform(get("/api/security/method-admin")
                            .header("Authorization", basic("reader", "reader-password")))
                    .andReturn().getResponse().getStatus();

            PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
            String encoded = encoder.encode("lesson-password");
            System.out.println("Password hash differs and matches: "
                    + (!encoded.equals("lesson-password") && encoder.matches("lesson-password", encoded)));
            System.out.println("Public endpoint -> " + publicStatus);
            System.out.println("Anonymous employee request -> " + anonymousStatus);
            System.out.println("USER can read employees -> " + readerStatus);
            System.out.println("USER can create employee -> " + readerCreateStatus);
            System.out.println("ADMIN can create employee -> " + adminCreateStatus);
            System.out.println("Method security rejects USER -> " + methodSecurityStatus);
        }

        System.out.println("PHASE 10 COMPLETE");
    }

    public static AnnotationConfigWebApplicationContext openContext(String databaseName) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.getEnvironment().getPropertySources().addFirst(
                new MapPropertySource("phase10-properties", Map.of(
                        "phase8.database-name", databaseName)));
        context.register(SpringSecurityConfiguration.class);
        context.refresh();
        return context;
    }

    private static String basic(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(
                credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static String validEmployeeJson(String email) {
        return "{"
                + "\"fullName\":\"Security Example\","
                + "\"email\":\"" + email + "\","
                + "\"salary\":7000.00,"
                + "\"hiredOn\":\"2025-01-20\","
                + "\"departmentName\":\"Engineering\""
                + "}";
    }
}
