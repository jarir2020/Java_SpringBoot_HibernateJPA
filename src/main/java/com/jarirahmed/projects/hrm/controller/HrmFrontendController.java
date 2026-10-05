package com.jarirahmed.projects.hrm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Keeps the browser entry point stable while static assets remain cacheable. */
@Controller
public class HrmFrontendController {
    @GetMapping({"/project5", "/project5/"})
    public String index() {
        return "forward:/project5/index.html";
    }
}
