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

/**
 * Controller responsible for displaying and updating the current user's profile.
 * <p>
 * Exposes endpoints under {@code /profile} to:
 * <ul>
 *     <li>Render the profile page with account and membership data.</li>
 *     <li>Add a membership to the currently authenticated account.</li>
 *     <li>Remove a membership from the currently authenticated account.</li>
 * </ul>
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final AccountService accountService;
    private final MembershipService membershipService;

  /**
   * Creates a new {@code ProfileController} with the required services.
   *
   * @param accountService    service for loading and updating {@link Account} entities
   * @param membershipService service for loading {@link Membership} entities
   */
    public ProfileController(AccountService accountService, MembershipService membershipService) {
        this.accountService = accountService;
        this.membershipService = membershipService;
    }

  /**
   * Handles GET requests to {@code /profile} and populates the model
   * with the current user's account and membership information.
   * <p>
   * The following attributes are added to the model:
   * <ul>
   *     <li>{@code isAuthenticated} – {@code true} if a user is logged in, otherwise {@code false}.</li>
   *     <li>{@code account} – the current {@link Account}, or {@code null} if unauthenticated.</li>
   *     <li>{@code memberships} – the current account's memberships, or {@code null} if unauthenticated.</li>
   *     <li>{@code allMemberships} – all available memberships for display (e.g., to join).</li>
   * </ul>
   *
   * @param model the MVC model used to expose attributes to the {@code profile} view
   * @return the logical view name for the profile page ({@code "profile"})
   */
    @GetMapping
    public String profile(Model model) {
        Optional<Account> current = getCurrentAccount();
        model.addAttribute("isAuthenticated", current.isPresent());
        model.addAttribute("account", current.orElse(null));
        model.addAttribute("memberships", current.map(Account::getMemberships).orElse(null));
        model.addAttribute("allMemberships", membershipService.getAllMemberships());
        return "profile";
    }


  /**
   * Adds the specified membership to the currently authenticated user.
   * <p>
   * Returns a JSON payload containing the updated account username and memberships.
   *
   * @param membershipId the ID of the {@link Membership} to add
   * @return a {@link ResponseEntity} containing either:
   * <ul>
   *     <li>HTTP 401 with a simple message if the user is not authenticated</li>
   *     <li>HTTP 200 with a JSON payload describing the updated account</li>
   * </ul>
   * @throws IllegalArgumentException if the membership with the given ID cannot be found
   */
    @PostMapping("/memberships/add")
    @ResponseBody
    public ResponseEntity<?> addMembership(@RequestParam("membershipId") Long membershipId) {
        Optional<Account> current = getCurrentAccount();
        //security check
        if (current.isEmpty()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        Membership membership = membershipService.findById(membershipId).orElseThrow(() -> new IllegalArgumentException("Membership not found"));
        accountService.addMembership(current.get(), membership);
        return ResponseEntity.ok((payload(current.get()))); //ResponseEntity serializes and returns it as a JSON
    }


  /**
   * Removes the specified membership from the currently authenticated user.
   * <p>
   * Returns a JSON payload containing the updated account username and memberships.
   *
   * @param membershipId the ID of the {@link Membership} to remove
   * @return a {@link ResponseEntity} containing either:
   * <ul>
   *     <li>HTTP 401 with a simple message if the user is not authenticated</li>
   *     <li>HTTP 200 with a JSON payload describing the updated account</li>
   * </ul>
   * @throws IllegalArgumentException if the membership with the given ID cannot be found
   */
    @PostMapping("/memberships/remove")
    @ResponseBody
    public ResponseEntity<?> removeMembership(@RequestParam("membershipId") Long membershipId) {
        Optional<Account> current = getCurrentAccount();
        //security check
        if (current.isEmpty()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        Membership membership = membershipService.findById(membershipId).orElseThrow(() -> new IllegalArgumentException("Membership not found"));
        accountService.removeMembership(current.get(), membership);
        return ResponseEntity.ok((payload(current.get())));//ResponseEntity serializes and returns it as a JSON
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
   * <p>
   * The resulting map includes:
   * <ul>
   *     <li>{@code username} – the account's username</li>
   *     <li>{@code memberships} – the account's memberships</li>
   * </ul>
   *
   * @param account the account to convert into a JSON-like payload
   * @return a map representing the account in a JSON-serializable structure
   */
    private Map<String, Object> payload(Account account) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", account.getUsername());
        map.put("memberships", account.getMemberships());
        return map;
    }

}
