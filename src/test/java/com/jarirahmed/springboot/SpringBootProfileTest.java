package com.jarirahmed.springboot;

import com.jarirahmed.springboot.config.CourseProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Confirms that application-dev.yml overrides the default course mode. */
@SpringBootTest(
        classes = SpringBootLearningApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("dev")
class SpringBootProfileTest {
    @Autowired
    private CourseProperties courseProperties;

    @Test
    void dev_profile_overrides_the_default_course_mode() {
        assertEquals("development", courseProperties.mode());
    }
}
