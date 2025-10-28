package com.example.perkmanager.controllers;

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

    // List all the current User's perks in a table, with options to sort and filter
    @GetMapping
    public String listPerks(
            @RequestParam Optional<String> membershipType,
            @RequestParam Optional<String> region,
            @RequestParam Optional<Boolean> activeOnly,
            @RequestParam Optional<String> sort,
            @RequestParam Optional<String> direction, // new param
            Model model) {
        // TODO: Add pagination for large perk lists, or otherwise re-organize the table set-up

        List<Perk> perks = perkService.filterPerks(membershipType, region, activeOnly, Optional.empty());

        // Apply sorting if requested
        if (sort.isPresent()) {
            boolean asc = !"desc".equalsIgnoreCase(direction.orElse("asc"));
            switch (sort.get()) {
                case "rating": // TODO: Ensure rating sorting works correctly once voting is implemented.
                    perks.sort(Comparator.comparingInt(Perk::getRating));
                    if (!asc) Collections.reverse(perks);
                    break;
                case "expiry":
                    perks.sort(Comparator.comparing(
                            Perk::getExpiryDate,
                            Comparator.nullsFirst(Comparator.comparingLong(Calendar::getTimeInMillis))
                    ));
                    if ("desc".equalsIgnoreCase(direction.orElse("asc"))) {
                        Collections.reverse(perks);
                    }
                    break;
            }
        }

        model.addAttribute("perks", perks);
        model.addAttribute("membershipType", membershipType.orElse(""));
        model.addAttribute("region", region.orElse(""));
        model.addAttribute("activeOnly", activeOnly.orElse(false));
        model.addAttribute("sort", sort.orElse(""));
        model.addAttribute("direction", direction.orElse("asc"));

        return "perks";
    }

    // Show form to add a new perk
    @GetMapping("/add")
    public String showAddPerkForm(Model model) {
        model.addAttribute("perk", new Perk());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("memberships", membershipService.getAllMemberships());

        return "add-perk";
    }

    // Handle add perk submission
    @PostMapping("/add")
    public String addPerk(@RequestParam("benefit") String benefit,
                          @RequestParam("productId") Long productId,
                          @RequestParam("membershipId") Long membershipId,
                          @RequestParam(value = "region", required = false) String region,
                          @RequestParam(value = "expiryDate", required = false) String expiryDate,
                          Model model) {

        try {
            // NOTE: Currently using a demo account for creation.
            // TODO: Replace with actual logged-in account once authentication is implemented.
            Account creator = accountService.findByUsername("Account 1 test")
                    .orElseThrow(() -> new RuntimeException("Demo account not found"));

            Product product = productService.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Membership membership = membershipService.findById(membershipId)
                    .orElseThrow(() -> new RuntimeException("Membership not found"));

            // Parse expiry date if provided
            Calendar cal = null;
            if (expiryDate != null && !expiryDate.isEmpty()) {
                String[] parts = expiryDate.split("-");
                cal = Calendar.getInstance();
                cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), 0, 0, 0);
            }

            Perk perk = perkService.createPerk(creator, membership, product, benefit, cal, region);

            System.out.println("Perk added: id=" + perk.getId());

            // TODO: Consider redirecting to the newly created perk's detail page
            return "redirect:/perks";

        } catch (Exception e) {
            e.printStackTrace(); // TODO: improve stack trace for debugging
            model.addAttribute("error", "Failed to add perk: " + e.getMessage());
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("memberships", membershipService.getAllMemberships());

            // TODO: Preserve form input values when returning after error
            return "add-perk";
        }
    }

    // TODO: Implement perk upvote/downvote endpoints
    // TODO: Implement perk edit/delete endpoints
}
