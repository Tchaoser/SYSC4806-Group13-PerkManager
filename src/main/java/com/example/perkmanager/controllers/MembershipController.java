package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Membership;
import com.example.perkmanager.services.MembershipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    // List all memberships
    @GetMapping
    public String listMemberships(Model model) {
        model.addAttribute("memberships", membershipService.getAllMemberships());
        return "memberships";
    }

    // Show the "add membership" form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        // we just pass an empty Membership so Thymeleaf can bind fields if needed
        model.addAttribute("membership", new Membership());
        return "add-membership";
    }

    // Handle form submit to create a membership
    @PostMapping("/add")
    public String addMembership(
            @RequestParam String type,
            @RequestParam("organizationName") String organizationName,
            @RequestParam String description) {
        membershipService.createMembership(type, organizationName, description);
        return "redirect:/memberships";
    }
}
