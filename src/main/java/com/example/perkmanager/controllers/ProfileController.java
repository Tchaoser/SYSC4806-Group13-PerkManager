package com.example.perkmanager.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.Product;
import com.example.perkmanager.services.AccountService;
import com.example.perkmanager.services.MembershipService;
import com.example.perkmanager.services.PerkService;
import com.example.perkmanager.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final AccountService accountService;
    private final MembershipService membershipService;

    public ProfileController(AccountService accountService, MembershipService membershipService) {
        this.accountService = accountService;
        this.membershipService = membershipService;
    }

    @GetMapping
    public String profile(Model model) {
        Optional<Account> current = getCurrentAccount();
        model.addAttribute("isAuthenticated", current.isPresent());
        model.addAttribute("account", current.orElse(null));

        var memberships = current.map(Account::getMemberships).orElseGet(java.util.Collections::emptySet);
        var availableMemberships = membershipService.getAllMemberships().stream()
                .filter(m -> memberships.stream().noneMatch(u -> u.getId().equals(m.getId())))
                .collect(java.util.stream.Collectors.toSet());

        model.addAttribute("memberships", memberships);
        model.addAttribute("allMemberships", availableMemberships);

        return "profile";
    }

    //adding membership to current user
    @PostMapping("/memberships/add")
    public String addMembership(@RequestParam("membershipId") Long membershipId) {
        Optional<Account> current = getCurrentAccount();
        //security check
        if (current.isEmpty()) {
            return "redirect:/login";
        }
        Membership membership = membershipService.findById(membershipId)
                .orElseThrow(() -> new IllegalArgumentException("Membership not found"));
        accountService.addMembership(current.get(), membership);
        return "redirect:/profile";
    }

    //removing a membership from a user
    @PostMapping("/memberships/remove")
    public String removeMembership(@RequestParam("membershipId") Long membershipId) {
        Optional<Account> current = getCurrentAccount();
        //security check
        if (current.isEmpty()) {
            return "redirect:/login";
        }
        Membership membership = membershipService.findById(membershipId)
                .orElseThrow(() -> new IllegalArgumentException("Membership not found"));
        accountService.removeMembership(current.get(), membership);
        return "redirect:/profile";
    }

    //function which returns only authenticated account of user and returns empty for the unauthenticated
    private Optional<Account> getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }
        return accountService.findByUsername(auth.getName());
    }

    //Builds a JSON-friendly map containing the account's username and memberships for API responses.
    private Map<String, Object> payload(Account account) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", account.getUsername());
        map.put("memberships", account.getMemberships());
        return map;
    }
}
