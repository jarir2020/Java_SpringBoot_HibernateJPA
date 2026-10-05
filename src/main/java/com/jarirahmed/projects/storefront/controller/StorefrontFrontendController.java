package com.jarirahmed.projects.storefront.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Makes `/project4` and `/project4/` convenient browser entry points. */
@Controller
public class StorefrontFrontendController {
    @GetMapping({"/project4", "/project4/"})
    public String index() {
        return "forward:/project4/index.html";
    }
}
