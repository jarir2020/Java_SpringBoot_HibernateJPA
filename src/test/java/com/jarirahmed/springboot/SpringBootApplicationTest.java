package com.jarirahmed.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies Boot auto-configuration, external properties, MVC, and Actuator. */
@SpringBootTest(
        classes = SpringBootLearningApplication.class,
        properties = {
                "course.name=Test Boot Course",
                "course.mode=test"
        })
@AutoConfigureMockMvc
class SpringBootApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void boot_binds_configuration_properties_and_scans_its_controller() throws Exception {
        mockMvc.perform(get("/api/boot/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.applicationName").value("java-spring-learning"))
                .andExpect(jsonPath("$.courseName").value("Test Boot Course"))
                .andExpect(jsonPath("$.courseMode").value("test"));
    }

    @Test
    void boot_auto_configuration_hosts_the_existing_phase_three_api() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .param("category", "Backend")
                        .param("search", "container")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "title,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].title").value("Spring Core Basics"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void actuator_exposes_a_safe_health_endpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void boot_supports_multipart_upload_and_binary_download() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "lesson.txt", "text/plain", "Boot file lesson".getBytes());

        MvcResult uploaded = mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("lesson.txt"))
                .andReturn();

        String location = uploaded.getResponse().getHeader("Location");
        assertTrue(location != null && location.startsWith("/api/files/"));

        MvcResult downloaded = mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"))
                .andReturn();
        assertEquals("Boot file lesson", downloaded.getResponse().getContentAsString());
    }
}
