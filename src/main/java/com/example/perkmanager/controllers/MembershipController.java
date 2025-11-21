package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Membership;
import com.example.perkmanager.services.MembershipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        model.addAttribute("membership", new Membership());
        return "add-membership";
    }

    // Handle form submit to create a membership
    @PostMapping("/add")
    public String addMembership(
            @RequestParam(required = false) String type,
            @RequestParam(value = "organizationName", required = false) String organizationName,
            @RequestParam(required = false) String description,
            Model model) {

        Map<String, String> fieldErrors = new HashMap<>();
        if (type == null || type.trim().isEmpty()) {
            fieldErrors.put("type", "Type is required");
        }
        if (organizationName == null || organizationName.trim().isEmpty()) {
            fieldErrors.put("organizationName", "Organization name is required");
        }
        if (description == null || description.trim().isEmpty()) {
            fieldErrors.put("description", "Description is required");
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("error", "Please complete all required fields");
            // keep the membership object for potential binding (optional)
            model.addAttribute("membership", new Membership());
            return "add-membership";
        }

        membershipService.createMembership(type.trim(), organizationName.trim(), description.trim());
        return "redirect:/memberships";
    }
}
