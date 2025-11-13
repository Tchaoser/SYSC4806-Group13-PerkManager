package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Membership;
import com.example.perkmanager.services.MembershipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling membership-related operations.
 * Manages the creation and listing of memberships (e.g., Air Miles, CAA, Visa).
 *
 * @author PerkManager Team
 * @version 1.0
 */
@Controller
@RequestMapping("/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    /**
     * Constructs a MembershipController with the specified MembershipService.
     *
     * @param membershipService the service for membership operations
     */
    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    /**
     * Lists all available memberships in the system.
     *
     * @param model the Spring model for passing data to the view
     * @return the name of the memberships template
     */
    @GetMapping
    public String listMemberships(Model model) {
        model.addAttribute("memberships", membershipService.getAllMemberships());
        return "memberships";
    }

    /**
     * Displays the form for adding a new membership.
     * Passes an empty Membership object to the view for form binding.
     *
     * @param model the Spring model for passing data to the view
     * @return the name of the add-membership template
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        // we just pass an empty Membership so Thymeleaf can bind fields if needed
        model.addAttribute("membership", new Membership());
        return "add-membership";
    }

    /**
     * Handles the submission of the add membership form.
     * Creates a new membership with the provided details and redirects to the memberships list.
     *
     * @param type the type of membership (e.g., Credit card, Air Miles)
     * @param organizationName the name of the organization or company (e.g., RBC, WestJet, Cineplex)
     * @param description the description or name of the membership (e.g., West Jet Rewards Member)
     * @return redirect to the memberships list page
     */
    @PostMapping("/add")
    public String addMembership(
            @RequestParam String type,
            @RequestParam("organizationName") String organizationName,
            @RequestParam String description
    ) {
        membershipService.createMembership(type, organizationName, description);
        return "redirect:/memberships";
    }
}
