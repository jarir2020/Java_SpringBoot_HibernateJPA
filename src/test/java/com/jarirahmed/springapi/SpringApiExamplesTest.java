package com.jarirahmed.springapi;

import com.jarirahmed.springapi.demo.SpringApiLesson;
import com.jarirahmed.springtransactions.SpringTransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/** Verifies DTO mapping, validation, layering, and the public API contract. */
class SpringApiExamplesTest {
    private AnnotationConfigWebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        context = SpringApiLesson.openContext("phase9-api-test");
        context.getBean(SpringTransactionService.class).seed();
        mockMvc = webAppContextSetup(context).build();
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void service_is_transactional_and_create_returns_response_dto() throws Exception {
        assertTrue(AopUtils.isAopProxy(context.getBean("employeeService")));

        String json = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/employees/4"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(json.contains("\"message\":\"Employee created.\""));
        assertTrue(json.contains("\"departmentName\":\"Engineering\""));
        assertTrue(json.contains("\"version\":0"));
        assertTrue(!json.contains("assignments"));
    }

    @Test
    void list_supports_filtering_pagination_and_envelope_metadata() throws Exception {
        String json = mockMvc.perform(get("/api/employees")
                        .param("department", "Engineering")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(json.contains("\"items\":["));
        assertTrue(json.contains("\"page\":0"));
        assertTrue(json.contains("\"size\":1"));
        assertTrue(json.contains("\"totalElements\":2"));
        assertTrue(json.contains("\"totalPages\":2"));
    }

    @Test
    void get_missing_employee_is_a_stable_not_found_error() throws Exception {
        String json = mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(json.contains("Employee 999 was not found."));
        assertTrue(json.contains("\"fieldErrors\":{}"));
    }

    @Test
    void invalid_request_returns_field_level_validation_errors() throws Exception {
        String json = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"\",\"email\":\"bad\","
                                + "\"salary\":0,\"hiredOn\":\"2099-01-01\","
                                + "\"departmentName\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(json.contains("Request validation failed."));
        assertTrue(json.contains("fullName"));
        assertTrue(json.contains("email"));
        assertTrue(json.contains("salary"));
        assertTrue(json.contains("hiredOn"));
        assertTrue(json.contains("departmentName"));
    }

    @Test
    void duplicate_email_is_a_conflict_and_unknown_department_is_not_found() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isCreated());

        String duplicate = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertTrue(duplicate.contains("already exists"));

        String missingDepartment = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()
                                .replace("david@example.com", "unknown@example.com")
                                .replace("Engineering", "Unknown")))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertTrue(missingDepartment.contains("Department 'Unknown' was not found."));
    }

    private static String validJson() {
        return "{"
                + "\"fullName\":\"David Ahmed\","
                + "\"email\":\"david@example.com\","
                + "\"salary\":7000.00,"
                + "\"hiredOn\":\"2025-01-20\","
                + "\"departmentName\":\"Engineering\""
                + "}";
    }
}
