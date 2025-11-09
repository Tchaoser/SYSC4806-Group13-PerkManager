package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.services.PerkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    private PerkService perkService;
    private HomeController homeController;
    private Model model;

    @BeforeEach
    void setUp() {
        perkService = mock(PerkService.class);
        homeController = new HomeController(perkService);
        model = mock(Model.class);
    }
    @Test
    void index_shouldAddTopRatedAndExpiringPerksToModel() {
        // Arrange: create mock perks
        Perk p1 = new Perk();
        p1.setBenefit("10% off movies");
        Calendar future1 = Calendar.getInstance();
        future1.add(Calendar.DAY_OF_YEAR, 5);
        p1.setExpiryDate(future1);

        Perk p2 = new Perk();
        p2.setBenefit("Free coffee");
        Calendar future2 = Calendar.getInstance();
        future2.add(Calendar.DAY_OF_YEAR, 2);
        p2.setExpiryDate(future2);

        Perk p3 = new Perk();
        p3.setBenefit("20% off flight");
        p3.setExpiryDate(null);

        // Create mock accounts for upvotes/downvotes
        Account account1 = new Account();
        account1.setUsername("user1");

        Account account2 = new Account();
        account2.setUsername("user2");

        Account account3 = new Account();
        account3.setUsername("user3");

        // Add upvotes/downvotes
        p1.addUpvote(account1);
        p1.addUpvote(account2);

        p2.addUpvote(account3);
        p2.addDownvote(account1);

        List<Perk> perks = Arrays.asList(p1, p2, p3);
        when(perkService.getAllPerks()).thenReturn(perks);

        // Act
        String view = homeController.index(0, 0, model);

        // Assert view name
        assertEquals("index", view);

        // Capture model attributes
        ArgumentCaptor<List> topRatedCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> expiringCaptor = ArgumentCaptor.forClass(List.class);

        verify(model).addAttribute(eq("featuredTopRated"), topRatedCaptor.capture());
        verify(model).addAttribute(eq("featuredExpiring"), expiringCaptor.capture());

        List<Perk> topRated = topRatedCaptor.getValue();
        List<Perk> expiring = expiringCaptor.getValue();

        // Top-rated perks should be sorted by rating descending, secondary by expiry
        assertEquals(3, topRated.size());
        assertEquals("10% off movies", topRated.get(0).getBenefit());

        // Expiring perks should be filtered to only future expiry, sorted by soonest expiry
        assertEquals(2, expiring.size());
        assertEquals("Free coffee", expiring.get(0).getBenefit());
        assertEquals("10% off movies", expiring.get(1).getBenefit());
    }
}
