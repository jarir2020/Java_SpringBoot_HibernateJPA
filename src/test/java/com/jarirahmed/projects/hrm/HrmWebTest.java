package com.jarirahmed.projects.hrm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Base64;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = HrmApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HrmWebTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy securityFilterChain;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(securityFilterChain)
                .build();
    }

    @Test
    void browser_dashboard_and_report_cache_are_available() throws Exception {
        mockMvc.perform(get("/project5/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/project5/index.html"));
        mockMvc.perform(get("/project5/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Orbit HR")));
        mockMvc.perform(get("/api/project5/dashboard?company=NSTAR")
                        .header("Authorization", basic("admin", "admin-password")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Northstar Labs")))
                .andExpect(content().string(containsString("\"cacheHit\":false")));
        mockMvc.perform(get("/api/project5/dashboard?company=NSTAR")
                        .header("Authorization", basic("admin", "admin-password")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"cacheHit\":true")));
    }

    @Test
    void roles_workflow_and_payroll_transaction_are_available() throws Exception {
        mockMvc.perform(get("/api/project5/dashboard?company=NSTAR"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/project5/payroll/runs")
                        .header("Authorization", basic("employee", "employee-password"))
                        .contentType("application/json")
                        .content("{\"companyCode\":\"NSTAR\",\"period\":\"" + YearMonth.now() + "\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/project5/attendance")
                        .header("Authorization", basic("employee", "employee-password"))
                        .contentType("application/json")
                        .content("{\"employeeId\":1,\"workDate\":\"" + LocalDate.now() + "\",\"status\":\"PRESENT\",\"hours\":8}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("PRESENT")));

        mockMvc.perform(post("/api/project5/payroll/runs")
                        .header("Authorization", basic("admin", "admin-password"))
                        .contentType("application/json")
                        .content("{\"companyCode\":\"NSTAR\",\"period\":\"" + YearMonth.now() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("PROCESSED")));
    }

    @Test
    void leave_approval_and_background_reminder_endpoints_work() throws Exception {
        mockMvc.perform(get("/api/project5/leave?company=NSTAR")
                        .header("Authorization", basic("admin", "admin-password")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PENDING")));

        mockMvc.perform(patch("/api/project5/leave/1/approve")
                        .header("Authorization", basic("hr", "hr-password")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("APPROVED")));

        mockMvc.perform(post("/api/project5/jobs/leave-reminders")
                        .header("Authorization", basic("admin", "admin-password")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("leave-reminders")));
    }

    private static String basic(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }
}
