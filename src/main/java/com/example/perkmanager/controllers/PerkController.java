

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
    public String addPerk(@RequestParam("benefit") String benefit,
                          @RequestParam("productId") Long productId,
                          @RequestParam("membershipId") Long membershipId,
                          @RequestParam(value = "region", required = false) String region,
                          @RequestParam(value = "expiryDate", required = false) String expiryDate,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
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

            Calendar cal = null;
            if (expiryDate != null && !expiryDate.isEmpty()) {
                String[] parts = expiryDate.split("-");
                cal = Calendar.getInstance();
                cal.set(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]) - 1,
                        Integer.parseInt(parts[2]),
                        0, 0, 0
                );
            }

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


