package com.example.perkmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling the homepage and root route of the Perk Manager application.
 * Serves as the entry point for users accessing the application.
 *
 * @author PerkManager Team
 * @version 1.0
 */
@Controller
public class HomeController {

    /**
     * Displays the homepage of the Perk Manager application.
     * Currently returns a basic index page. Future implementations may include
     * featured perks or user-specific recommendations.
     *
     * @return the name of the index template
     */
    @GetMapping("/")
    public String index() {
        // TODO: Later, we can pass featured perks or user-specific recommendations to the homepage
        return "index";
    }
}
