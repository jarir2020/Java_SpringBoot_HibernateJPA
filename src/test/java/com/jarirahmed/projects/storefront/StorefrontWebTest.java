package com.jarirahmed.projects.storefront;

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
import java.util.Base64;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = StorefrontApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StorefrontWebTest {
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
    void browser_frontend_and_public_jpa_catalog_are_available() throws Exception {
        mockMvc.perform(get("/project4/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/project4/index.html"));
        mockMvc.perform(get("/project4/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Lumen Market")));
        mockMvc.perform(get("/api/project4/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Focus Desk Lamp")));
    }

    @Test
    void authentication_cart_transaction_and_order_history_work_together() throws Exception {
        mockMvc.perform(get("/api/project4/cart"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/project4/cart/items")
                        .header("Authorization", basic("shopper", "shopper-password"))
                        .contentType("application/json")
                        .content("{\"productId\":1,\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Focus Desk Lamp")));

        mockMvc.perform(post("/api/project4/checkout")
                        .header("Authorization", basic("shopper", "shopper-password"))
                        .contentType("application/json")
                        .content("{\"shippingName\":\"Demo Shopper\",\"shippingEmail\":\"shopper@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("PAID")));

        mockMvc.perform(get("/api/project4/orders")
                        .header("Authorization", basic("shopper", "shopper-password")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Focus Desk Lamp")));
    }

    private static String basic(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }
}
