package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Perk;
import com.example.perkmanager.services.PerkService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PerkService perkService;

    public HomeController(PerkService perkService) {
        this.perkService = perkService;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(value = "topPage", defaultValue = "0") int topPage,
            @RequestParam(value = "expPage", defaultValue = "0") int expPage,
            Model model) {

        int pageSize = 4;
        List<Perk> allPerks = perkService.getAllPerks();

        // Top-rated perks with secondary sort by expiry
        List<Perk> topRated = allPerks.stream()
                .sorted(
                        Comparator.comparingInt(Perk::getRating).reversed()
                                .thenComparing(
                                        p -> p.getExpiryDate() != null ? p.getExpiryDate().getTime() : new Date(Long.MAX_VALUE)
                                )
                )
                .collect(Collectors.toList());
        int topTotalPages = (int) Math.ceil((double) topRated.size() / pageSize);
        int topStart = topPage * pageSize;
        int topEnd = Math.min(topStart + pageSize, topRated.size());
        List<Perk> featuredTopRated = topRated.subList(topStart, topEnd);

        // Soon-to-expire perks with secondary sort by rating
        Calendar now = Calendar.getInstance();
        List<Perk> expiringSoon = allPerks.stream()
                .filter(p -> p.getExpiryDate() != null && p.getExpiryDate().after(now))
                .sorted(
                        Comparator.comparing((Perk p) -> p.getExpiryDate().getTime()) // specify Perk type
                                .thenComparing(Comparator.comparingInt(Perk::getRating).reversed())
                )
                .collect(Collectors.toList());
        int expTotalPages = (int) Math.ceil((double) expiringSoon.size() / pageSize);
        int expStart = expPage * pageSize;
        int expEnd = Math.min(expStart + pageSize, expiringSoon.size());
        List<Perk> featuredExpiring = expiringSoon.subList(expStart, expEnd);

        // Add attributes for Thymeleaf
        model.addAttribute("featuredTopRated", featuredTopRated);
        model.addAttribute("topPage", topPage);
        model.addAttribute("topTotalPages", topTotalPages);

        model.addAttribute("featuredExpiring", featuredExpiring);
        model.addAttribute("expPage", expPage);
        model.addAttribute("expTotalPages", expTotalPages);

        return "index";
    }
}
