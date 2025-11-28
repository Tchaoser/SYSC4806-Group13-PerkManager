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
  private final PerkService perkService;


  /**
   * Creates a new {@code ProfileController} with the required services.
   *
   * @param accountService    service for loading and updating {@link Account} entities
   * @param membershipService service for loading {@link Membership} entities
   * @param perkService   service for loading and updating {@link Perk} entities
   */
  public ProfileController(AccountService accountService, MembershipService membershipService, PerkService perkService) {
    this.accountService = accountService;
    this.membershipService = membershipService;
    this.perkService = perkService;
  }

  /**
   * Handles GET requests to {@code /profile} and populates the model
   * with the current user's account and membership and perk information.
   *
   * @param model the MVC model used to expose attributes to the {@code profile} view
   * @return the logical view name for the profile page ({@code "profile"})
   */
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

    var perks = current.map(Account::getSavedPerks)
      .orElseGet(java.util.Collections::emptySet);

    var availablePerks = perkService.getAllPerks().stream()
      .filter(p -> perks.stream().noneMatch(sp -> sp.getId().equals(p.getId())))
      .collect(java.util.stream.Collectors.toSet());

    model.addAttribute("perks", perks);
    model.addAttribute("allPerks", availablePerks);
    return "profile";
  }

  /**
   * Adds the specified membership to the currently authenticated user.
   * <p>
   * Returns a JSON payload containing the updated account username and memberships.
   *
   * @param membershipId the ID of the {@link Membership} to add
   * @return a redirect to login
   * @throws IllegalArgumentException if the membership with the given ID cannot be found
   */
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

  /**
   * Removes the specified membership from the currently authenticated user.
   * <p>
   * Returns a JSON payload containing the updated account username and memberships.
   *
   * @param membershipId the ID of the {@link Membership} to remove
   * @return a redirect to login
   * @throws IllegalArgumentException if the membership with the given ID cannot be found
   */
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

  /**
   * Adds the specified perk to the currently authenticated user's profile.
   * <p>
   *  This endpoint expects a {@code perkId} referring to an existing {@link Perk}.
   *  If the user is not authenticated, the request is redirected to the login page.
   *  If the perk cannot be found, an {@link IllegalArgumentException} is thrown.
   * <p>
   * After successfully adding the perk, the user is redirected back to the
   * profile page.
   *
   * @param perkId the ID of the {@link Perk} to add to the user's saved perks
   * @return a redirect to {@code /profile} (or {@code /login} if unauthenticated)
   * @throws IllegalArgumentException if no perk exists for the provided ID
   */
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

  /**
   * Removes the specified perk from the currently authenticated user's profile.
   * <p>
   * This endpoint expects a {@code perkId} referring to an existing {@link Perk}.
   * If the user is not authenticated, the request is redirected to the login page.
   * If the perk cannot be found, an {@link IllegalArgumentException} is thrown.
   * <p>
   * After successfully removing the perk, the user is redirected back to the
   * profile page.
   *
   * @param perkId the ID of the {@link Perk} to remove from the user's saved perks
   * @return a redirect to {@code /profile} (or {@code /login} if unauthenticated)
   * @throws IllegalArgumentException if no perk exists for the provided ID
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
   * @return authenticated account of user and returns empty for the unauthenticated
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
   * @return a map representing the account in a JSON-friendly map structure
   */
  private Map<String, Object> payload(Account account) {
    Map<String, Object> map = new HashMap<>();
    map.put("username", account.getUsername());
    map.put("memberships", account.getMemberships());
    map.put("perks", account.getSavedPerks());
    return map;
  }
}
