package com.jarirahmed.springsecurity;

import com.jarirahmed.springsecurity.demo.SpringSecurityLesson;
import com.jarirahmed.springtransactions.SpringTransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/** Verifies password hashing, authentication, roles, and method security. */
class SpringSecurityExamplesTest {
    private AnnotationConfigWebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        context = SpringSecurityLesson.openContext("phase10-security-test");
        context.getBean(SpringTransactionService.class).seed();
        mockMvc = webAppContextSetup(context)
                .addFilters(context.getBean(FilterChainProxy.class))
                .build();
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void password_encoder_stores_a_one_way_hash() {
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
        String encoded = encoder.encode("course-password");

        assertNotEquals("course-password", encoded);
        assertTrue(encoder.matches("course-password", encoded));
    }

    @Test
    void public_endpoint_is_open_but_employee_api_returns_json_401_without_credentials()
            throws Exception {
        mockMvc.perform(get("/api/security/public"))
                .andExpect(status().isOk());

        String json = mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(json.contains("Authentication is required."));
        assertTrue(json.contains("\"status\":401"));
        assertTrue(json.contains("/api/employees"));
    }

    @Test
    void user_can_read_but_only_admin_can_create() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .header("Authorization", basic("reader", "reader-password")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/employees")
                        .header("Authorization", basic("reader", "reader-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson("reader-forbidden@example.com")))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/employees")
                        .header("Authorization", basic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson("admin-allowed@example.com")))
                .andExpect(status().isCreated());
    }

    @Test
    void method_security_requires_admin_even_after_authentication() throws Exception {
        mockMvc.perform(get("/api/security/method-admin")
                        .header("Authorization", basic("reader", "reader-password")))
                .andExpect(status().isForbidden());

        String json = mockMvc.perform(get("/api/security/method-admin")
                        .header("Authorization", basic("admin", "admin-password")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertTrue(json.contains("Method security allowed this request."));
    }

    @Test
    void invalid_basic_credentials_return_json_401() throws Exception {
        String json = mockMvc.perform(get("/api/security/me")
                        .header("Authorization", basic("reader", "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(json.contains("Authentication is required."));
        assertTrue(json.contains("\"status\":401"));
    }

    private static String basic(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(
                credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static String validJson(String email) {
        return "{"
                + "\"fullName\":\"Security Test\","
                + "\"email\":\"" + email + "\","
                + "\"salary\":7000.00,"
                + "\"hiredOn\":\"2025-01-20\","
                + "\"departmentName\":\"Engineering\""
                + "}";
    }
}
