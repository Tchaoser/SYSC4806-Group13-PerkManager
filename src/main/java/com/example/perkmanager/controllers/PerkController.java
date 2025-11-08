

package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.Product;
import com.example.perkmanager.services.AccountService;
import com.example.perkmanager.services.MembershipService;
import com.example.perkmanager.services.PerkService;
import com.example.perkmanager.services.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/perks")
public class PerkController {

    private final PerkService perkService;
    private final ProductService productService;
    private final MembershipService membershipService;
    private final AccountService accountService;

    public PerkController(PerkService perkService,
                          ProductService productService,
                          MembershipService membershipService,
                          AccountService accountService) {
        this.perkService = perkService;
        this.productService = productService;
        this.membershipService = membershipService;
        this.accountService = accountService;
    }

    // List all perks with sorting + filtering + pagination
    @GetMapping
    public String listPerks(
            @RequestParam Optional<String> membershipType,
            @RequestParam Optional<String> region,
            @RequestParam Optional<Boolean> expiryOnly,
            @RequestParam Optional<String> sort,
            @RequestParam Optional<String> direction,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size,
            Model model) {
        try {
            // Base filtered list
            List<Perk> perks = perkService.filterPerks(membershipType, region, expiryOnly, Optional.empty());
            // Sorting
            perks = perkService.sortPerks(perks, sort, direction);
            // Pagination
            int pageNum = Math.max(page.orElse(0), 0);
            int pageSize = Math.max(size.orElse(10), 1);
            int total = perks.size();
            int totalPages = (int) Math.ceil((double) total / pageSize);
            if (totalPages == 0) totalPages = 1;
            if (pageNum >= totalPages) pageNum = totalPages - 1;
            int from = pageNum * pageSize;
            int to = Math.min(from + pageSize, total);
            List<Perk> pageItems = perks.subList(from, to);

            model.addAttribute("perks", pageItems);
            model.addAttribute("membershipType", membershipType.orElse(""));
            model.addAttribute("region", region.orElse(""));
            model.addAttribute("expiryOnly", expiryOnly.orElse(false));
            model.addAttribute("sort", sort.orElse(""));
            model.addAttribute("direction", direction.orElse("asc"));
            model.addAttribute("page", pageNum);
            model.addAttribute("size", pageSize);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalPerks", total);

            return "perks";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to load perks: " + e.getMessage());
            return "perks";
        }
    }

    // Show form to add a new perk
    @GetMapping("/add")
    public String showAddPerkForm(Model model) {
        model.addAttribute("perk", new Perk());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("memberships", membershipService.getAllMemberships());
        return "add-perk";
    }

    // Create a perk using authenticated user
    @PostMapping("/add")
    public String addPerk(@RequestParam(required = false) Long productId,
                                               @RequestParam(required = false) Long membershipId,
                                               @RequestParam(required = false) String benefit,
                                               @RequestParam(required = false) String region,
                                               @RequestParam(required = false) String expiryDate,
                                               @AuthenticationPrincipal UserDetails userDetails, Model model) {

        Map<String, String> fieldErrors = new HashMap<>();
        if (benefit == null || benefit.trim().isEmpty()) {
            fieldErrors.put("benefit", "Benefit is required");
        }
        if (productId == null) {
            fieldErrors.put("productId", "Please select a product");
        }
        if (membershipId == null) {
            fieldErrors.put("membershipId", "Please select a membership");
        }

        Calendar cal = null;
        if (expiryDate != null && !expiryDate.isEmpty()) {
            try {
                String[] parts = expiryDate.split("-");
                cal = Calendar.getInstance();
                cal.set(Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]) - 1,
                        Integer.parseInt(parts[2]),
                        0, 0, 0);
                // ensure expiry is today or later
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                if (cal.before(today)) {
                    fieldErrors.put("expiryDate", "Expiry date cannot be in the past.");
                }
            } catch (Exception parseEx) {
                fieldErrors.put("expiryDate", "Invalid date.");
            }
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("memberships", membershipService.getAllMemberships());
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("error", "Please complete all required fields");
            return "add-perk";
        }

        try {
            if (userDetails == null) {
                throw new RuntimeException("Please log in to add a perk.");
            }
            Account creator = accountService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Authenticated account not found"));
            Product product = productService.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Membership membership = membershipService.findById(membershipId)
                    .orElseThrow(() -> new RuntimeException("Membership not found"));

            perkService.createPerk(creator, membership, product, benefit, cal, region);
            return "redirect:/perks";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to add perk: " + e.getMessage());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("memberships", membershipService.getAllMemberships());
            return "add-perk";
        }
    }


    // --- Voting endpoints (update counts immediately, then redirect back) ---

    @PostMapping("/{id}/upvote")
    public String upvote(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return "redirect:/login";
            }
            Account account = accountService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Authenticated account not found"));
            perkService.upvotePerk(id, account);
            return "redirect:/perks";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/perks";
        }
    }

    @PostMapping("/{id}/downvote")
    public String downvote(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return "redirect:/login";
            }
            Account account = accountService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Authenticated account not found"));
            perkService.downvotePerk(id, account);
            return "redirect:/perks";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/perks";
        }
    }

    // TODO: add edit/delete
}


