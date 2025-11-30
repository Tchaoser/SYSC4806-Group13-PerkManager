package com.example.perkmanager.controllers;

import com.example.perkmanager.services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling account-related operations including user registration and login.
 * Manages the signup and login pages for the Perk Manager application.
 *
 */
@Controller
public class AccountController {

    private final AccountService accountService;

    /**
     * Constructs an AccountController with the specified AccountService.
     *
     * @param accountService the service for account operations
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Displays the signup page for new user registration.
     *
     * @param model the Spring model for passing data to the view
     * @return the name of the signup template
     */
    @GetMapping("/signup")
    public String displaySignup(Model model) {
        return "signup";
    }

    /**
     * Handles user registration by creating a new account.
     * Redirects to the login page on success, or returns to signup page with error message on failure.
     *
     * @param username the desired username for the new account
     * @param password the password for the new account
     * @param model    the Spring model for passing data to the view
     * @return redirect to login page on success, or signup template with error on failure
     */
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {
        try {
            accountService.createAccount(username, password);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    /**
     * Displays the login page for existing users.
     *
     * @param model the Spring model for passing data to the view
     * @return the name of the login template
     */
    @GetMapping("/login")
    public String displayLogin(Model model) {
        return "login";
    }
}