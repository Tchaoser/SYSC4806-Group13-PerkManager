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

        // Trim inputs (avoid null pointer by checking null)
        String typeTrim = (type != null) ? type.trim() : null;
        String orgTrim = (organizationName != null) ? organizationName.trim() : null;
        String descTrim = (description != null) ? description.trim() : null;

        Map<String, String> fieldErrors = new HashMap<>();

        // Required checks (kept) and max length checks
        if (typeTrim == null || typeTrim.isEmpty()) {
            fieldErrors.put("type", "Type is required");
        } else if (typeTrim.length() > 100) {
            fieldErrors.put("type", "Type must be at most 100 characters");
        }

        if (orgTrim == null || orgTrim.isEmpty()) {
            fieldErrors.put("organizationName", "Organization name is required");
        } else if (orgTrim.length() > 100) {
            fieldErrors.put("organizationName", "Organization name must be at most 100 characters");
        }

        if (descTrim == null || descTrim.isEmpty()) {
            fieldErrors.put("description", "Description is required");
        } else if (descTrim.length() > 500) {
            fieldErrors.put("description", "Description must be at most 500 characters");
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("error", "Please fix the errors below");
            model.addAttribute("membership", new Membership());
            return "add-membership";
        }

        membershipService.createMembership(typeTrim, orgTrim, descTrim);
        return "redirect:/memberships";
    }
}
