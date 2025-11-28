package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Membership;
import com.example.perkmanager.services.MembershipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling membership-related operations.
 * Manages the creation and listing of memberships (e.g., Air Miles, CAA, Visa).
 *
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
   * @param model the Spring model for passing data to the view
   * @return redirect to the memberships list page or add membership page depending on certain conditions
   */
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
