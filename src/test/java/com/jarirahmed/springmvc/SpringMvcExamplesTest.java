package com.jarirahmed.springmvc;

import com.jarirahmed.springmvc.container.SpringMvcApplication;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

class SpringMvcExamplesTest {
    private AnnotationConfigWebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        context = SpringMvcApplication.createContext();
        mockMvc = webAppContextSetup(context).build();
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void get_binds_query_header_and_cookie_and_returns_json() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/posts")
                        .param("category", "Backend")
                        .param("published", "true")
                        .header("X-Client-Name", "test-client")
                        .cookie(new Cookie("course-mode", "guided"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertTrue(json.contains("\"clientName\":\"test-client\""));
        assertTrue(json.contains("\"courseMode\":\"guided\""));
        assertTrue(json.contains("\"title\":\"Spring Core Basics\""));
    }

    @Test
    void post_deserializes_json_validates_it_and_returns_created_location() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-Name", "test-client")
                        .content(validJson("MVC Post", false)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals("/api/posts/3", result.getResponse().getHeader("Location"));
        assertEquals("test-client", result.getResponse().getHeader("X-Client-Name"));
        assertTrue(result.getResponse().getContentAsString().contains("\"title\":\"MVC Post\""));
    }

    @Test
    void invalid_body_returns_field_level_validation_errors() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"body\":\"short\",\"authorEmail\":\"bad\",\"category\":\"\",\"published\":false}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertTrue(json.contains("\"message\":\"Request validation failed.\""));
        assertTrue(json.contains("\"title\""));
        assertTrue(json.contains("\"authorEmail\""));
        assertTrue(json.contains("\"category\""));
    }

    @Test
    void custom_cross_field_validation_rejects_short_published_posts() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson("Published But Short", true, "short body")))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("at least 40 characters"));
    }

    @Test
    void put_patch_and_delete_map_to_expected_statuses() throws Exception {
        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson("Updated Post", false)))
                .andExpect(status().isOk());

        MvcResult patched = mockMvc.perform(patch("/api/posts/1/publication")
                        .param("published", "false"))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(patched.getResponse().getContentAsString().contains("\"published\":false"));

        mockMvc.perform(delete("/api/posts/2"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/posts/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void global_advice_maps_not_found_and_conflict_errors() throws Exception {
        MvcResult missing = mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound())
                .andReturn();
        assertTrue(missing.getResponse().getContentAsString().contains("Post 999 was not found."));

        MvcResult conflict = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson("Spring Core Basics", false)))
                .andExpect(status().isConflict())
                .andReturn();
        assertTrue(conflict.getResponse().getContentAsString().contains("already exists"));
    }

    @Test
    void session_state_survives_across_requests_using_the_same_session() throws Exception {
        MockHttpSession session = new MockHttpSession();

        MvcResult first = mockMvc.perform(get("/api/posts/session").session(session))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult second = mockMvc.perform(get("/api/posts/session").session(session))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(first.getResponse().getContentAsString().contains("\"visits\":1"));
        assertTrue(second.getResponse().getContentAsString().contains("\"visits\":2"));
    }

    private static String validJson(String title, boolean published) {
        return validJson(title, published,
                "This request body is long enough for the MVC lesson.");
    }

    private static String validJson(String title, boolean published, String body) {
        return "{" +
                "\"title\":\"" + title + "\"," +
                "\"body\":\"" + body + "\"," +
                "\"authorEmail\":\"writer@example.com\"," +
                "\"category\":\"Backend\"," +
                "\"published\":" + published +
                "}";
    }
}
