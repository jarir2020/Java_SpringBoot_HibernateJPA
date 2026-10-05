package com.jarirahmed.springmvc.demo;

import com.jarirahmed.springmvc.container.SpringMvcApplication;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.stream.Collectors;

/** Runs representative Spring MVC requests through DispatcherServlet. */
public final class SpringMvcLesson {
    private SpringMvcLesson() {
    }

    public static void run() throws Exception {
        System.out.println("\n=== PHASE 3: SPRING MVC ===");

        try (AnnotationConfigWebApplicationContext context = SpringMvcApplication.createContext()) {
            MockMvc mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();

            MvcResult list = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                            .param("category", "Backend")
                            .header("X-Client-Name", "cli-demo")
                            .cookie(new jakarta.servlet.http.Cookie("course-mode", "guided"))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            System.out.println("GET /api/posts -> " + list.getResponse().getStatus());
            System.out.println("Response: " + list.getResponse().getContentAsString());

            String body = "{" +
                    "\"title\":\"MVC Request Lesson\"," +
                    "\"body\":\"This body is long enough to demonstrate a published request.\"," +
                    "\"authorEmail\":\"teacher@example.com\"," +
                    "\"category\":\"Backend\"," +
                    "\"published\":true" +
                    "}";
            MvcResult created = mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Client-Name", "cli-demo")
                            .content(body))
                    .andReturn();
            System.out.println("POST /api/posts -> " + created.getResponse().getStatus());
            System.out.println("Location: " + created.getResponse().getHeader("Location"));

            MvcResult invalid = mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"\",\"body\":\"short\",\"authorEmail\":\"bad\",\"category\":\"\",\"published\":false}"))
                    .andReturn();
            System.out.println("Invalid POST -> " + invalid.getResponse().getStatus());
            System.out.println("Validation fields: " + invalid.getResponse().getContentAsString()
                    .lines().collect(Collectors.joining(" ")));
        }

        System.out.println("PHASE 3 COMPLETE");
    }
}
