package com.jarirahmed.projects.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BlogProjectApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BlogProjectWebTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void browser_frontend_and_seeded_backend_are_available() throws Exception {
        mockMvc.perform(get("/project3/"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl(
                        "/project3/index.html"));

        mockMvc.perform(get("/project3/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Spring MVC Blog Studio")));

        mockMvc.perform(get("/api/project3/posts"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Why Spring MVC still matters")));
    }

    @Test
    void api_validates_and_creates_user_post_and_comment() throws Exception {
        mockMvc.perform(post("/api/project3/users")
                        .contentType("application/json")
                        .content("{\"displayName\":\"New Reader\",\"email\":\"reader@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/project3/users/3"));

        mockMvc.perform(post("/api/project3/posts")
                        .contentType("application/json")
                        .content("{\"title\":\"A browser-created post\",\"body\":\"This post was created through the Project 3 REST API.\",\"authorId\":3,\"published\":true}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/project3/posts/3"));

        mockMvc.perform(post("/api/project3/posts/3/comments")
                        .contentType("application/json")
                        .content("{\"userId\":3,\"body\":\"The browser and backend are connected.\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("The browser and backend are connected.")));

        mockMvc.perform(post("/api/project3/users")
                        .contentType("application/json")
                        .content("{\"displayName\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("fieldErrors")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("displayName")));
    }
}
