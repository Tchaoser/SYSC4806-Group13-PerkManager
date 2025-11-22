package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Membership;
import com.example.perkmanager.services.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class MembershipControllerTest {

    private MembershipController membershipController;
    private MembershipService membershipService;
    private Model model;

    @BeforeEach
    void setup() {
        membershipService = mock(MembershipService.class);
        membershipController = new MembershipController(membershipService);
        model = mock(Model.class);
    }

    @Test
    void addMembership() {
        String description = "test description";
        String type = "test type";
        String org = "test organization";

        String view = membershipController.addMembership(type, org, description, model);

        assertEquals("redirect:/memberships", view);
        verify(membershipService, times(1)).createMembership(type, org, description);
    }

    @Test
    void addMembershipTooLong() {
        String longType = "a".repeat(101); // assumes max 100
        String longOrg = "b".repeat(101);
        String longDesc = "c".repeat(201); // assumes max 200

        String view = membershipController.addMembership(longType, longOrg, longDesc, model);

        assertEquals("add-membership", view);
        verify(model).addAttribute(eq("fieldErrors"), any(Map.class));
        verifyNoInteractions(membershipService); // service should not be called
    }

    @Test
    void addMembershipTrimsInput() {
        String type = "  My Type  ";
        String org = "  Org Name  ";
        String desc = "  Desc  ";

        membershipController.addMembership(type, org, desc, model);

        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> orgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);

        verify(membershipService).createMembership(typeCaptor.capture(), orgCaptor.capture(), descCaptor.capture());

        assertEquals("My Type", typeCaptor.getValue());
        assertEquals("Org Name", orgCaptor.getValue());
        assertEquals("Desc", descCaptor.getValue());
    }

    @Test
    void showAddForm() {
        String view = membershipController.showAddForm(model);

        assertEquals("add-membership", view);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(model).addAttribute(eq("membership"), captor.capture());

        Membership membership = captor.getValue();
        assertNotNull(membership);
    }

    @Test
    void listMemberships() {
        Membership membership = new Membership();
        membership.setDescription("test description");
        membership.setType("test type");
        membership.setOrganizationName("test organization");

        when(membershipService.getAllMemberships()).thenReturn(List.of(membership));

        String view = membershipController.listMemberships(model);

        assertEquals("memberships", view);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("memberships"), captor.capture());

        List<Membership> membershipsAdded = captor.getValue();
        assertEquals(1, membershipsAdded.size());
        Membership m = membershipsAdded.get(0);
        assertEquals("test description", m.getDescription());
        assertEquals("test type", m.getType());
        assertEquals("test organization", m.getOrganizationName());
    }
}
