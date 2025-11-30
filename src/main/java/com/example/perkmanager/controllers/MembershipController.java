package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.services.AccountService;
import com.example.perkmanager.services.MembershipService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for handling membership-related operations such as
 * displaying available memberships, adding memberships, and saving
 * memberships to a user's account.
 */
@Controller
@RequestMapping("/memberships")
public class MembershipController {

    private final MembershipService membershipService;
    private final AccountService accountService;

    /**
     * Constructs a MembershipController with required services.
     *
     * @param membershipService service used for membership CRUD operations
     * @param accountService    service used for user account operations
     */
    public MembershipController(MembershipService membershipService, AccountService accountService) {
        this.membershipService = membershipService;
        this.accountService = accountService;
    }

    /**
     * Lists all available memberships, along with user save-states if authenticated.
     * Also prepares JSON metadata for dynamic frontend interactions.
     *
     * @param userDetails the current authenticated user (if any)
     * @param model       Spring model used to pass attributes to the view
     * @return the memberships view template
     */
    @GetMapping
    public String listMemberships(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            final Account currentUser = (userDetails != null)
                    ? accountService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                    : null;

            List<Membership> memberships = membershipService.getAllMemberships();
            memberships.sort(Comparator.comparing(Membership::getOrganizationName));

            Map<Long, Integer> saveStates = new HashMap<>();
            if (currentUser != null) {
                for (Membership m : memberships) {
                    saveStates.put(m.getId(), currentUser.hasMembership(m.getId()) ? 1 : 0);
                }
            }

            model.addAttribute("saveStates", saveStates);
            model.addAttribute("memberships", memberships);
            model.addAttribute("isAuthenticated", currentUser != null);

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> membershipsJsonList = memberships.stream().map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("type", m.getType());
                map.put("organization", m.getOrganizationName());
                map.put("description", m.getDescription());

                int saveState = (currentUser != null && currentUser.hasMembership(m.getId())) ? 1 : 0;
                map.put("saveState", saveState);

                var csrf = model.getAttribute("_csrf");
                if (csrf instanceof org.springframework.security.web.csrf.CsrfToken token) {
                    map.put("csrfParam", "_csrf");
                    map.put("csrfToken", token.getToken());
                    map.put("csrfHeader", token.getHeaderName());
                } else {
                    map.put("csrfParam", "_csrf");
                    map.put("csrfToken", "");
                    map.put("csrfHeader", "");
                }

                map.put("isAuthenticated", currentUser != null);

                return map;
            }).collect(Collectors.toList());

            String membershipsJson = mapper.writeValueAsString(membershipsJsonList);
            model.addAttribute("membershipsJson", membershipsJson);

            return "memberships";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to load memberships: " + e.getMessage());
            return "memberships";
        }
    }

    /**
     * Displays the form for adding a new membership.
     *
     * @param model Spring model for passing the form-binding Membership object
     * @return the add-membership template
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("membership", new Membership());
        return "add-membership";
    }

    /**
     * Handles submission of the new membership creation form.
     * Validates input fields and creates a membership if valid.
     *
     * @param type             the type of membership (e.g., Credit Card, Air Miles)
     * @param organizationName the organization providing the membership
     * @param description      a descriptive name or details for the membership
     * @param model            Spring model used for returning validation errors
     * @return redirect to memberships list on success, or back to form on validation failure
     */
    @PostMapping("/add")
    public String addMembership(
            @RequestParam(required = false) String type,
            @RequestParam(value = "organizationName", required = false) String organizationName,
            @RequestParam(required = false) String description,
            Model model) {

        String typeTrim = (type != null) ? type.trim() : null;
        String orgTrim = (organizationName != null) ? organizationName.trim() : null;
        String descTrim = (description != null) ? description.trim() : null;

        Map<String, String> fieldErrors = new HashMap<>();
        if (typeTrim == null || typeTrim.isEmpty()) {
            fieldErrors.put("type", "Type is required");
        } else if (typeTrim.length() > Membership.TYPE_MAX_LENGTH) {
            fieldErrors.put("type", "Type must be at most " + Membership.TYPE_MAX_LENGTH + " characters");
        }

        if (orgTrim == null || orgTrim.isEmpty()) {
            fieldErrors.put("organizationName", "Organization name is required");
        } else if (orgTrim.length() > Membership.ORG_NAME_MAX_LENGTH) {
            fieldErrors.put("organizationName", "Organization name must be at most " + Membership.ORG_NAME_MAX_LENGTH + " characters");
        }

        if (descTrim == null || descTrim.isEmpty()) {
            fieldErrors.put("description", "Description is required");
        } else if (descTrim.length() > Membership.DESCRIPTION_MAX_LENGTH) {
            fieldErrors.put("description", "Description must be at most " + Membership.DESCRIPTION_MAX_LENGTH + " characters");
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

    /**
     * Toggles whether a membership is saved to the authenticated user's profile.
     * If the user has the membership, it is removed; otherwise, it is added.
     *
     * @param id          the ID of the membership to toggle
     * @param userDetails the authenticated user's details
     * @return redirect back to the memberships page
     */
    @PostMapping("/{id}/save")
    public String toggleSaveMembership(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return "redirect:/login";
            }

            Account account = accountService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Authenticated account not found"));

            Membership membership = membershipService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Membership not found"));

            if (account.hasMembership(membership.getId())) {
                accountService.removeMembership(account, membership);
            } else {
                accountService.addMembership(account, membership);
            }

            return "redirect:/memberships";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/memberships";
        }
    }
}
