package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.services.AccountService;
import com.example.perkmanager.services.MembershipService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/memberships")
public class MembershipController {

    private final MembershipService membershipService;
    private final AccountService accountService;

    public MembershipController(MembershipService membershipService, AccountService accountService) {
        this.membershipService = membershipService;
        this.accountService = accountService;
    }

    // List all memberships
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
                    //Handle save states
                    if (currentUser.hasMembership(m.getId())) {
                        saveStates.put(m.getId(), 1);
                    } else {
                        saveStates.put(m.getId(), 0);
                    }
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

                int saveState = 0;
                if (currentUser != null) {
                    if (currentUser.hasPerk(m.getId())) saveState = 1;
                }

                map.put("saveState", saveState);
                map.put("csrfParam", "_csrf");
                map.put("csrfToken", model.getAttribute("_csrf") != null ? ((org.springframework.security.web.csrf.CsrfToken) model.getAttribute("_csrf")).getToken() : "");
                map.put("csrfHeader", model.getAttribute("_csrf") != null ? ((org.springframework.security.web.csrf.CsrfToken) model.getAttribute("_csrf")).getHeaderName() : "");
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

    /**
     * Used to save memberships to user profile from the memberships page.
     *
     * @param id The is of the membership to be added.
     * @param userDetails The information of the user.
     * @return The html package to redirect the user to.
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

            if (account.hasMembership(membership.getId())){
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

