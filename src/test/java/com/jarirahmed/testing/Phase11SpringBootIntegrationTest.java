package com.jarirahmed.testing;

import com.jarirahmed.springtransactions.SpringTransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test: Boot starts the context, MockMvc sends HTTP, and H2 stores
 * real JPA entities behind the controller/service/repository layers.
 */
@SpringBootTest(
        classes = Phase11IntegrationApplication.class,
        properties = {
                "phase8.database-name=phase11-boot-integration",
                "spring.main.allow-bean-definition-overriding=true"
        })
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Phase11SpringBootIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringTransactionService transactionService;

    @BeforeAll
    void seedDatabase() {
        transactionService.seed();
    }

    @Test
    void boot_and_mockmvc_reach_the_real_database_backed_api() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("department", "Engineering")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.items[0].fullName").value("Alice Rahman"))
                .andExpect(jsonPath("$.data.items[0].departmentName").value("Engineering"));
    }

    @Test
    void integration_test_exercises_validation_and_http_status_mapping() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed."));
    }

    @Test
    void integration_test_exercises_a_successful_create_and_location_header() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEmployeeJson()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/employees/4"))
                .andExpect(jsonPath("$.data.fullName").value("Phase Eleven Student"));
    }

    private static String validEmployeeJson() {
        return "{"
                + "\"fullName\":\"Phase Eleven Student\","
                + "\"email\":\"phase11@example.com\","
                + "\"salary\":7000.00,"
                + "\"hiredOn\":\"2025-01-20\","
                + "\"departmentName\":\"Engineering\""
                + "}";
    }
}
