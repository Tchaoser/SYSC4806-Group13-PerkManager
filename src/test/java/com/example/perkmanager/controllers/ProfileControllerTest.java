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
        verify(model).addAttribute(eq("allMemberships"), any());
        verify(model).addAttribute(eq("perks"), any());
        verify(model).addAttribute(eq("allPerks"), any());

    }

    @Test
    @DisplayName("profile() filters out already-linked memberships from allMemberships")
    void profileFiltersLinkedMemberships() {
        // arrange: account with one linked membership (id=1)
        Account acc = new Account();
        acc.setUsername("alice");

        Membership linked = new Membership();
        linked.setId(1L);
        Set<Membership> linkedSet = new HashSet<>();
        linkedSet.add(linked);
        acc.getMemberships().addAll(linkedSet);

        // all memberships contains linked (id=1) and another available (id=2)
        Membership available = new Membership();
        available.setId(2L);

        when(accountService.findByUsername("alice")).thenReturn(Optional.of(acc));
        when(membershipService.getAllMemberships())
                .thenReturn(List.of(linked, available));

        // set authentication so getCurrentAccount() will find 'alice'
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "ignored")
        );

        // act
        String view = profileController.profile(model);

        // assert view
        assertEquals("profile", view);

        // capture what was put into model for "memberships" and "allMemberships"
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set> membershipsCaptor = ArgumentCaptor.forClass(Set.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set> allMembershipsCaptor = ArgumentCaptor.forClass(Set.class);

        verify(model).addAttribute(eq("memberships"), membershipsCaptor.capture());
        verify(model).addAttribute(eq("allMemberships"), allMembershipsCaptor.capture());

        Set<Membership> membershipsInModel = membershipsCaptor.getValue();
        Set<Membership> availableInModel = allMembershipsCaptor.getValue();

        // the user's linked memberships should be present
        assertTrue(membershipsInModel.contains(linked));
        // availableInModel should contain only the membership not already linked
        assertEquals(1, availableInModel.size());
        assertTrue(availableInModel.contains(available));
        assertFalse(availableInModel.contains(linked));
    }

    @Test
    @DisplayName("addMembership() redirects to /login when unauthenticated")
    void addMembershipRedirectsWhenGuest() {
        SecurityContextHolder.clearContext();
        String redirect = profileController.addMembership(5L);
        assertEquals("redirect:/login", redirect);
    }

    @Test
    @DisplayName("addMembership() calls AccountService.addMembership and redirects when authenticated")
    void addMembershipCallsService() {
        Account acc = new Account();
        acc.setUsername("bob");
        Membership m = new Membership();
        m.setId(11L);

        when(accountService.findByUsername("bob")).thenReturn(Optional.of(acc));
        when(membershipService.findById(11L)).thenReturn(Optional.of(m));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("bob", "pw")
        );

        String redirect = profileController.addMembership(11L);
        assertEquals("redirect:/profile", redirect);
        verify(accountService).addMembership(eq(acc), eq(m));
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
