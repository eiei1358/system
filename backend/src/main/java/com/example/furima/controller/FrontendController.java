package com.example.furima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FrontendController {

    @GetMapping({"/", "/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}
