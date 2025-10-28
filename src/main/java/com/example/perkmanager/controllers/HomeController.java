package com.example.perkmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        // TODO: Later, we can pass featured perks or user-specific recommendations to the homepage
        return "index";
    }
}
