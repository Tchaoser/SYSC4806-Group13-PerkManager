package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.services.AccountService;
import com.example.perkmanager.services.MembershipService;
import com.example.perkmanager.services.PerkService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final AccountService accountService;
    private final MembershipService membershipService;
    private final PerkService perkService;

    public ProfileController(AccountService accountService, MembershipService membershipService, PerkService perkService) {
        this.accountService = accountService;
        this.membershipService = membershipService;
        this.perkService = perkService;
    }

    @GetMapping
    public String profile(Model model) {
        Optional<Account> current = getCurrentAccount();
        model.addAttribute("isAuthenticated", current.isPresent());
        model.addAttribute("account", current.orElse(null));

        List<Membership> memberships = new ArrayList<>(
                current.map(Account::getMemberships)
                        .orElseGet(java.util.Collections::emptySet)
        );

        memberships.sort(Comparator.comparing(Membership::getOrganizationName));
        model.addAttribute("memberships", memberships);

        List<Perk> perks = new ArrayList<>(
                current.map(Account::getSavedPerks)
                        .orElseGet(java.util.Collections::emptySet)
        );

        perks.sort(Comparator.comparing(perk -> perk.getMembership().getOrganizationName()));
        model.addAttribute("perks", perks);
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

    // Perk endpoints
    @PostMapping("/perks/add")
    public String addPerk(@RequestParam("perkId") Long perkId) {
        Optional<Account> current = getCurrentAccount();
        if (current.isEmpty()) {
            return "redirect:/login";
        }

        Perk perk = perkService.findById(perkId)
                .orElseThrow(() -> new IllegalArgumentException("Perk not found"));

        accountService.addPerkToProfile(current.get(), perk);
        return "redirect:/profile";
    }

    @PostMapping("/perks/remove")
    public String removePerk(@RequestParam("perkId") Long perkId) {
        Optional<Account> current = getCurrentAccount();
        if (current.isEmpty()) {
            return "redirect:/login";
        }

        Perk perk = perkService.findById(perkId)
                .orElseThrow(() -> new IllegalArgumentException("Perk not found"));

        accountService.removePerkFromProfile(current.get(), perk);
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
        map.put("perks", account.getSavedPerks());
        return map;
    }
}
