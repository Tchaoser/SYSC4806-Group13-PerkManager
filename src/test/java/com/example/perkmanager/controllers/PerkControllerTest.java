package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.Product;
import com.example.perkmanager.services.AccountService;
import com.example.perkmanager.services.MembershipService;
import com.example.perkmanager.services.PerkService;
import com.example.perkmanager.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PerkControllerTest {

    private PerkController perkController;
    private PerkService perkService;
    private MembershipService membershipService;
    private ProductService productService;
    private AccountService accountService;
    private Model model;

    @BeforeEach
    void setup() {
        perkService = mock(PerkService.class);
        membershipService = mock(MembershipService.class);
        productService = mock(ProductService.class);
        accountService = mock(AccountService.class);
        perkController = new PerkController(perkService, productService, membershipService, accountService);
        model = mock(Model.class);
    }

    @Test
    void listPerks() {
        Perk perk = new Perk();
        perk.setBenefit("10% off");
        List<Perk> perkList = List.of(perk);

        when(perkService.filterPerks(any(), any(), any(), any())).thenReturn(perkList);
        when(perkService.sortPerks(any(), any(), any())).thenReturn(perkList);

        String view = perkController.listPerks(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                model
        );

        assertEquals("perks", view);
        verify(model).addAttribute("perks", perkList);
        verify(model).addAttribute("page", 0);
        verify(model).addAttribute("size", 10);
        verify(model).addAttribute("totalPages", 1);
        verify(model).addAttribute("totalPerks", perkList.size());
    }

    @Test
    void showAddPerkForm() {
        List<Product> products = List.of(new Product());
        List<Membership> memberships = List.of(new Membership());
        when(productService.getAllProducts()).thenReturn(products);
        when(membershipService.getAllMemberships()).thenReturn(memberships);

        String view = perkController.showAddPerkForm(model);

        assertEquals("add-perk", view);
        verify(model).addAttribute(eq("perk"), any(Perk.class));
        verify(model).addAttribute("products", products);
        verify(model).addAttribute("memberships", memberships);
    }

    @Test
    void addPerk() {
        List<Product> products = List.of(new Product());
        List<Membership> memberships = List.of(new Membership());
        when(productService.getAllProducts()).thenReturn(products);
        when(membershipService.getAllMemberships()).thenReturn(memberships);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");

        Account account = new Account();
        Product product = new Product();
        Membership membership = new Membership();

        when(accountService.findByUsername("user")).thenReturn(Optional.of(account));
        when(productService.findById(1L)).thenReturn(Optional.of(product));
        when(membershipService.findById(1L)).thenReturn(Optional.of(membership));

        String successView = perkController.addPerk(
                1L, 1L, "Free Coffee", null, null,
                userDetails, model
        );

        assertEquals("redirect:/perks", successView);
        verify(perkService).createPerk(account, membership, product, "Free Coffee", null, null);

        reset(model, perkService);

        String failView = perkController.addPerk(
                1L, 1L, "", null, null,
                userDetails, model
        );

        assertEquals("add-perk", failView);
        verify(model).addAttribute(eq("fieldErrors"), any(Map.class));
        verify(model).addAttribute("products", products);
        verify(model).addAttribute("memberships", memberships);
        verifyNoInteractions(perkService);
    }

    @Test
    void upvote() {
        Account account = new Account();
        when(accountService.findByUsername(anyString())).thenReturn(Optional.of(account));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");

        String view = perkController.upvote(1L, userDetails);

        assertEquals("redirect:/perks", view);
        verify(perkService).upvotePerk(1L, account);
    }

    @Test
    void downvote() {
        Account account = new Account();
        when(accountService.findByUsername(anyString())).thenReturn(Optional.of(account));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");

        String view = perkController.downvote(1L, userDetails);

        assertEquals("redirect:/perks", view);
        verify(perkService).downvotePerk(1L, account);
    }
}
