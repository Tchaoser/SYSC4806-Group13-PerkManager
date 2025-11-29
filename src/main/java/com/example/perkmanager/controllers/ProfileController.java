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

/**
 * Controller responsible for displaying and updating the current user's profile.
 * <p>
 * Exposes endpoints under {@code /profile} to:
 * <ul>
 *     <li>Render the profile page with account, membership, and perk data.</li>
 *     <li>Add or remove memberships for the authenticated user.</li>
 *     <li>Add or remove perks for the authenticated user.</li>
 * </ul>
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final AccountService accountService;
    private final MembershipService membershipService;
    private final PerkService perkService;

    /**
     * Creates a new {@code ProfileController} with the required services.
     *
     * @param accountService    service for loading and updating {@link Account} entities
     * @param membershipService service for loading {@link Membership} entities
     * @param perkService       service for loading and updating {@link Perk} entities
     */
    public ProfileController(AccountService accountService, MembershipService membershipService, PerkService perkService) {
        this.accountService = accountService;
        this.membershipService = membershipService;
        this.perkService = perkService;
    }

    /**
     * Handles GET requests to {@code /profile} and populates the model
     * with the current user's account, memberships, and perks.
     * <p>
     * Memberships and perks are sorted alphabetically by organization name.
     *
     * @param model the MVC model used to expose attributes to the {@code profile} view
     * @return the logical view name for the profile page ({@code "profile"})
     */
    @GetMapping
    public String profile(Model model) {
        Optional<Account> current = getCurrentAccount();
        model.addAttribute("isAuthenticated", current.isPresent());
        model.addAttribute("account", current.orElse(null));

        List<Membership> memberships = new ArrayList<>(
                current.map(Account::getMemberships)
                        .orElseGet(Collections::emptySet)
        );

        memberships.sort(Comparator.comparing(Membership::getOrganizationName));
        model.addAttribute("memberships", memberships);

        List<Perk> perks = new ArrayList<>(
                current.map(Account::getSavedPerks)
                        .orElseGet(Collections::emptySet)
        );

        perks.sort(Comparator.comparing(perk -> perk.getMembership().getOrganizationName()));
        model.addAttribute("perks", perks);

        return "profile";
    }

    /**
     * Removes the specified membership from the currently authenticated user.
     *
     * @param membershipId the ID of the {@link Membership} to remove
     * @return a redirect to {@code /profile} (or {@code /login} if unauthenticated)
     * @throws IllegalArgumentException if the membership with the given ID cannot be found
     */
    @PostMapping("/memberships/remove")
    public String removeMembership(@RequestParam("membershipId") Long membershipId) {
        Optional<Account> current = getCurrentAccount();
        if (current.isEmpty()) {
            return "redirect:/login";
        }

        Membership membership = membershipService.findById(membershipId)
                .orElseThrow(() -> new IllegalArgumentException("Membership not found"));

        accountService.removeMembership(current.get(), membership);
        return "redirect:/profile";
    }

    /**
     * Removes the specified perk from the currently authenticated user's profile.
     *
     * @param perkId the ID of the {@link Perk} to remove
     * @return a redirect to {@code /profile} (or {@code /login} if unauthenticated)
     * @throws IllegalArgumentException if the perk with the given ID cannot be found
     */
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

    /**
     * Retrieves the currently authenticated {@link Account}, if any.
     * <p>
     * This method inspects the Spring Security {@link SecurityContextHolder} and:
     * <ul>
     *     <li>Returns {@link Optional#empty()} if no user is authenticated or the user is anonymous.</li>
     *     <li>Otherwise, looks up the {@link Account} by username via {@link AccountService}.</li>
     * </ul>
     *
     * @return an {@link Optional} containing the authenticated account, or empty if unauthenticated
     */
    private Optional<Account> getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }
        return accountService.findByUsername(auth.getName());
    }

    /**
     * Builds a JSON-friendly {@link Map} containing minimal account information
     * for API responses.
     *
     * @param account the account to convert into a JSON-like payload
     * @return a map containing the account's username, memberships, and perks
     */
    private Map<String, Object> payload(Account account) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", account.getUsername());
        map.put("memberships", account.getMemberships());
        map.put("perks", account.getSavedPerks());
        return map;
    }
}
