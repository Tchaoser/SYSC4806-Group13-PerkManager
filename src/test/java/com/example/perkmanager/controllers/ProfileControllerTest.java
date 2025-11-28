package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.services.AccountService;
import com.example.perkmanager.services.MembershipService;
import com.example.perkmanager.services.PerkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class ProfileControllerTest {

    private ProfileController profileController;
    private AccountService accountService;
    private MembershipService membershipService;
    private PerkService perkService;
    private Model model;

    @BeforeEach
    void setup() {
        accountService = mock(AccountService.class);
        membershipService = mock(MembershipService.class);
        perkService = mock(PerkService.class);
        model = mock(Model.class);

        profileController = new ProfileController(accountService, membershipService, perkService);
        // ensure no leftover auth between tests
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("profile() returns profile view and shows guest when unauthenticated")
    void profileAsGuest() {
        // no authentication in SecurityContext => guest
        SecurityContextHolder.clearContext();

        String view = profileController.profile(model);
        assertEquals("profile", view);

        // verify model attributes for guest
        verify(model).addAttribute("isAuthenticated", false);
        verify(model).addAttribute("account", null);
        // memberships and allMemberships get added as well (empty)
        verify(model).addAttribute(eq("memberships"), any());
        verify(model).addAttribute(eq("perks"), any());
    }

    @Test
    @DisplayName("removeMembership() redirects to /login when unauthenticated")
    void removeMembershipRedirectsWhenGuest() {
        SecurityContextHolder.clearContext();
        String redirect = profileController.removeMembership(7L);
        assertEquals("redirect:/login", redirect);
    }

    @Test
    @DisplayName("removeMembership() calls AccountService.removeMembership and redirects when authenticated")
    void removeMembershipCallsService() {
        Account acc = new Account();
        acc.setUsername("bob");
        Membership m = new Membership();
        m.setId(22L);

        when(accountService.findByUsername("bob")).thenReturn(Optional.of(acc));
        when(membershipService.findById(22L)).thenReturn(Optional.of(m));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("bob", "pw")
        );

        String redirect = profileController.removeMembership(22L);
        assertEquals("redirect:/profile", redirect);
        verify(accountService).removeMembership(eq(acc), eq(m));
    }
}
