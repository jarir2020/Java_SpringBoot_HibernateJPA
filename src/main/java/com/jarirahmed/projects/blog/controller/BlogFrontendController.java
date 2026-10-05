package com.jarirahmed.projects.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Makes both `/project3` and `/project3/` convenient browser entry points. */
@Controller
public class BlogFrontendController {
    @GetMapping({"/project3", "/project3/"})
    public String index() {
        return "forward:/project3/index.html";
    }
}
